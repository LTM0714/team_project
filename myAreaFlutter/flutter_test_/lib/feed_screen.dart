import 'package:flutter/material.dart';

class FeedScreen extends StatelessWidget {
  const FeedScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 실제 게시물 리스트는 ListView.builder 등으로 구현
    return Scaffold(
      appBar: AppBar(
        title: Text('피드'),
      ),
      body: ListView(
        children: [
          // 예시 게시물
          ListTile(
            leading: CircleAvatar(child: Icon(Icons.person)),
            title: Text('사용자 이름'),
            subtitle: Text('여기에 게시글 내용'),
          ),
          // 추가 게시물...
        ],
      ),
    );
  }
}
