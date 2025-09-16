import 'package:flutter/material.dart';

class SearchScreen extends StatelessWidget {
  const SearchScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 예시로 GridView (인스타그램 스타일)
    return Scaffold(
      appBar: AppBar(title: Text('검색')),
      body: GridView.count(
        crossAxisCount: 3,
        children: List.generate(30, (index) {
          return Container(
            margin: EdgeInsets.all(2),
            color: Colors.grey[300],
            child: Center(child: Text('사진 ${index + 1}')),
          );
        }),
      ),
    );
  }
}
