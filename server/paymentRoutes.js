const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const QRCode = require("qrcode");
const { v4: uuidv4 } = require("uuid");
const axios = require("axios");

// Mô hình thanh toán
const PaymentSchema = new mongoose.Schema({
  licensePlate: String,
  transactionId: String,
  amount: Number,
  status: { type: String, enum: ["pending", "paid"], default: "pending" },
  createdAt: { type: Date, default: Date.now },
});

const Payment = mongoose.model("Payment", PaymentSchema);

// 1. API tạo mã QR thanh toán
router.post("/generate-qr", async (req, res) => {
  try {
    const { licensePlate, amount } = req.body;
    if (!licensePlate || !amount) {
      return res.status(400).json({ message: "Thiếu thông tin biển số hoặc số tiền" });
    }

    const transactionId = uuidv4();

    // Lưu vào DB
    const newPayment = new Payment({ licensePlate, transactionId, amount });
    await newPayment.save();

    // Tạo mã QR
    const qrData = `${process.env.PAYMENT_URL}/confirm?transactionId=${transactionId}`;
    const qrCode = await QRCode.toDataURL(qrData);

    res.json({
      message: "QR Code đã tạo thành công",
      transactionId,
      qrCode,
    });
  } catch (error) {
    res.status(500).json({ message: "Lỗi khi tạo QR", error });
  }
});

// 2. API xác nhận thanh toán
router.post("/confirm", async (req, res) => {
  try {
    const { transactionId, paymentMethod } = req.body;
    if (!transactionId || !paymentMethod) {
      return res.status(400).json({ message: "Thiếu transactionId hoặc phương thức thanh toán" });
    }

    const payment = await Payment.findOne({ transactionId, status: "pending" });
    if (!payment) {
      return res.status(404).json({ message: "Giao dịch không tồn tại hoặc đã thanh toán" });
    }

    // Xử lý thanh toán (Momo/VNPAY giả lập)
    const paymentGatewayResponse = await processPayment(payment.amount, paymentMethod);

    if (paymentGatewayResponse.success) {
      payment.status = "paid";
      await payment.save();
      res.json({ message: "Thanh toán thành công", transactionId });
    } else {
      res.status(400).json({ message: "Thanh toán thất bại", error: paymentGatewayResponse.error });
    }
  } catch (error) {
    res.status(500).json({ message: "Lỗi khi xác nhận thanh toán", error });
  }
});

// Hàm xử lý thanh toán giả lập
async function processPayment(amount, method) {
  try {
    if (method === "momo") {
      const response = await axios.post("https://momo.vn/api/payment", { amount });
      return { success: response.data.success, error: response.data.error };
    } else if (method === "vnpay") {
      const response = await axios.post("https://vnpay.vn/api/payment", { amount });
      return { success: response.data.success, error: response.data.error };
    } else {
      return { success: false, error: "Phương thức thanh toán không hợp lệ" };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
}

module.exports = router;
