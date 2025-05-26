const express = require('express');
const { MongoClient, ObjectId } = require('mongodb');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

const uri = "mongodb+srv://kiethuynhforwork:longtubi12345@cluster0.hw0qcee.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"; // Thay bằng connection string của bạn
const client = new MongoClient(uri);

async function run() {
    try {
        await client.connect();
        console.log("Connected to MongoDB Atlas");

        const database = client.db("auth_db");
        const users = database.collection("users");

        // Đăng ký
        app.post('/api/signup', async (req, res) => {
            const { email, password, name } = req.body;
            const existingUser = await users.findOne({ email });
            if (existingUser) {
                return res.status(400).send({ message: "Email already in use" });
            }
            const result = await users.insertOne({ email, password, name }); // Lưu ý: Nên mã hóa password
            res.status(201).send({ message: "User registered", userId: result.insertedId });
        });

        // Đăng nhập
        app.post('/api/signin', async (req, res) => {
            const { email, password } = req.body;
            const user = await users.findOne({ email, password }); // Lưu ý: Nên mã hóa password
            if (user) {
                res.status(200).send({ message: "Login successful", userId: user._id });
            } else {
                res.status(401).send({ message: "Invalid email or password" });
            }
        });

        app.listen(3000, () => {
            console.log("Server running on port 3000");
        });
    } catch (err) {
        console.error(err);
    }
}

run().catch(console.dir);