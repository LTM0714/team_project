import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'main.dart'; // MainTabNavigator import
import 'signup_screen.dart';

// UI 상태(_isLoading, _error)와 사용자 입력(_emailController, _passwordController)을 관리하는 클래스
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _error;

  Future<void> _login() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    final url = Uri.parse("http://ec2-43-201-70-69.ap-northeast-2.compute.amazonaws.com:8080/api/users/login");
    // HTTP POST 요청, json으로 서버에 전송
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "email": _emailController.text.trim(),
        "password": _passwordController.text.trim(),
      }),
    );

    setState(() {
      _isLoading = false;
    });

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);

      // 서버에서 반환한 accessToken/refreshToken을 SharedPreferences에 저장 -> 앱 전역에서 접근 가능
      // 메인 페이지, 게시물 업로드, 좋아요 등 모든 API 호출 시 Authorization: Bearer ${accessToken} 헤더에 accessToken을 전송
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString("accessToken", data["accessToken"]);
      await prefs.setString("refreshToken", data["refreshToken"]);

      if (!mounted) return;
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const MainTabNavigator()),
      );
    } else {
      setState(() {
        _error = "로그인 실패: ${response.body}";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("로그인")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: "이메일"),
            ),
            TextField(
              controller: _passwordController,
              decoration: const InputDecoration(labelText: "비밀번호"),
              obscureText: true,
            ),
            const SizedBox(height: 16),
            if (_error != null)
              Text(_error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _isLoading ? null : _login,
              child: _isLoading
                  ? const CircularProgressIndicator(color: Colors.white)
                  : const Text("로그인"),
            ),
            TextButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const SignupScreen()),
                );
              },
              child: const Text("회원가입하기"),
            ),
          ],
        ),
      ),
    );
  }
}
