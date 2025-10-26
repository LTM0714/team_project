import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';
import 'post_model.dart';
import 'like_model.dart';
import 'search_grid_screen.dart';
import 'create_screen.dart';
import 'activity_screen.dart';
import 'profile_screen.dart';
import 'search_tab.dart';
import 'login_screen.dart'; // 로그인 추가

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => LikeModel()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '지역기반 SNS',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const MainTabNavigator(), // 수정: MainTabNavigator부터 시작
      debugShowCheckedModeBanner: false,
    );
  }
}

class MainTabNavigator extends StatefulWidget {
  const MainTabNavigator({super.key});
  @override
  State<MainTabNavigator> createState() => _MainTabNavigatorState();
}

class _MainTabNavigatorState extends State<MainTabNavigator> {
  int _selectedIndex = 0;
  List<Post> myPosts = [];

  List<String> _interestRegions = [];
  File? _profileImage;
  String _intro = '자기소개를 입력하세요.';

  final List<String> _regions = [
    '강남', '강동', '강북', '강서', '관악', '광진', '구로', '금천', 
    '노원', '도봉', '동대문', '동작', '마포', '서대문', '서초', '성동', 
    '성북', '송파', '양천', '영등포', '용산', '은평', '종로', '중구', '중랑',
    
    '부산', '대구', '인천', '광주', '대전', '울산', '세종',
    
  ];

  void _onUpload(Post post) {
    setState(() {
      myPosts.add(post);
    });
  }

  void _onEditProfileImage(File? img) {
    setState(() {
      _profileImage = img;
    });
  }

  void _onEditIntro(String text) {
    setState(() {
      _intro = text;
    });
  }

  void _onEditInterestRegions(List<String> regions) {
    setState(() {
      _interestRegions = regions;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: [
        SearchGridScreen(
          posts: myPosts,
          interestRegions: _interestRegions,
        ),
        SearchTab(
          regions: _regions,
          interestRegions: _interestRegions,
          onEditInterestRegions: _onEditInterestRegions,
        ),
        CreateScreen(
          onUpload: _onUpload,
          regions: _regions,
        ),
        ActivityScreen(
          posts: myPosts,
        ),
        ProfileScreen(
          myPosts: myPosts,
          likedPosts: context.watch<LikeModel>().likedPosts,
          profileImage: _profileImage,
          onEditProfileImage: _onEditProfileImage,
          intro: _intro,
          onEditIntro: _onEditIntro,
          interestRegions: _interestRegions,
          onEditInterestRegions: _onEditInterestRegions,
          regions: _regions,
        ),
      ][_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (idx) => setState(() => _selectedIndex = idx),
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: '홈'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: '검색'),
          BottomNavigationBarItem(icon: Icon(Icons.add_box), label: '업로드'),
          BottomNavigationBarItem(icon: Icon(Icons.favorite), label: '활동'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: '프로필'),
        ],
      ),
    );
  }
}
