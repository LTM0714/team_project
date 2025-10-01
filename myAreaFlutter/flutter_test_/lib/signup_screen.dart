import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'login_screen.dart';

class SignupScreen extends StatefulWidget {
  const SignupScreen({super.key});

  @override
  State<SignupScreen> createState() => _SignupScreenState();
}

class _SignupScreenState extends State<SignupScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _nicknameController = TextEditingController();
  File? _profileImage;
  bool _isLoading = false;
  String? _error;

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final picked = await picker.pickImage(source: ImageSource.gallery);
    if (picked != null) {
      setState(() {
        _profileImage = File(picked.path);
      });
    }
  }

  Future<void> _signup() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    String? base64Image;
    if (_profileImage != null) {
      final bytes = await _profileImage!.readAsBytes();
      base64Image = base64Encode(bytes);
    }

    final url = Uri.parse("http://ec2-43-201-70-69.ap-northeast-2.compute.amazonaws.com:8080/api/users/signup");
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "email": _emailController.text.trim(),
        "password": _passwordController.text.trim(),
        "name": _nicknameController.text.trim(),
        "profileImage": base64Image, // 선택사항 (없으면 null)
      }),
    );

    setState(() {
      _isLoading = false;
    });

    if (response.statusCode == 200) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("회원가입 성공! 로그인 해주세요.")),
      );
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    } else {
      setState(() {
        _error = "회원가입 실패: ${response.statusCode}, ${response.body}";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("회원가입")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: SingleChildScrollView(
          child: Column(
            children: [
              TextField(controller: _emailController, decoration: const InputDecoration(labelText: "이메일")),
              TextField(controller: _passwordController, decoration: const InputDecoration(labelText: "비밀번호"), obscureText: true),
              TextField(controller: _nicknameController, decoration: const InputDecoration(labelText: "닉네임")),
              const SizedBox(height: 16),
              GestureDetector(
                onTap: _pickImage,
                child: _profileImage == null
                    ? Container(width: 100, height: 100, color: Colors.grey[300], child: const Icon(Icons.add_a_photo))
                    : Image.file(_profileImage!, width: 100, height: 100),
              ),
              const SizedBox(height: 16),
              if (_error != null) Text(_error!, style: const TextStyle(color: Colors.red)),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _isLoading ? null : _signup,
                child: _isLoading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text("회원가입"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
