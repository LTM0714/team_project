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
      home: const MainTabNavigator(),
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

  List<String> _interestRegions = ['서울'];
  File? _profileImage;
  String _intro = '자기소개를 입력하세요.';

  final List<String> _regions = [
    '서울', '서울-홍대', '서울-이태원', '서울-압구정', '서울-강남역', '서울-명동',
    '서울-건대입구', '서울-잠실', '서울-신촌', '서울-여의도', '서울-종로',
     '부산', '대구', '인천', '광주', '대전', '울산', '세종',
    '경기', '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주'
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

  // 관심지역은 최소 1개 이상 유지
  void _onEditInterestRegions(List<String> regions) {
    setState(() {
      if (regions.isNotEmpty) {
        _interestRegions = regions;
      }
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
