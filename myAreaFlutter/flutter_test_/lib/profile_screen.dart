import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'post_model.dart';
import 'post_detail_screen.dart';

class ProfileScreen extends StatefulWidget {
  final List<Post> myPosts;
  final List<Post> likedPosts; // ✅ 추가: 좋아요한 게시글 목록
  final File? profileImage;
  final void Function(File?) onEditProfileImage;
  final String intro;
  final void Function(String) onEditIntro;
  final List<String> interestRegions;
  final void Function(List<String>) onEditInterestRegions;
  final List<String> regions;

  const ProfileScreen({
    required this.myPosts,
    required this.likedPosts, // ✅ 추가
    required this.profileImage,
    required this.onEditProfileImage,
    required this.intro,
    required this.onEditIntro,
    required this.interestRegions,
    required this.onEditInterestRegions,
    required this.regions,
    super.key,
  });

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  bool _isEditingIntro = false;
  final TextEditingController _introController = TextEditingController();
  bool _showLiked = false; // ✅ 추가: 토글 상태 (false=내 게시글, true=좋아요한 게시글)

  void _pickProfileImage() async {
    final pickedFile = await ImagePicker().pickImage(source: ImageSource.gallery);
    if (pickedFile != null) {
      widget.onEditProfileImage(File(pickedFile.path));
    }
  }

  void _editIntro() {
    setState(() {
      _introController.text = widget.intro == '자기소개를 입력하세요.' ? '' : widget.intro;
      _isEditingIntro = true;
    });
  }

  void _saveIntro() {
    widget.onEditIntro(_introController.text.isEmpty ? '자기소개를 입력하세요.' : _introController.text);
    setState(() {
      _isEditingIntro = false;
    });
  }

  void _editInterestRegions() async {
    final result = await showDialog<List<String>>(
      context: context,
      builder: (ctx) {
        List<String> tempSelected = List.from(widget.interestRegions);
        return AlertDialog(
          title: Text('관심지역 선택'),
          content: StatefulBuilder(
            builder: (context, setState) {
              return SizedBox(
                width: double.maxFinite,
                child: SingleChildScrollView(
                  child: Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: widget.regions.map((region) {
                      final isSelected = tempSelected.contains(region);
                      return GestureDetector(
                        onTap: () {
                          setState(() {
                            if (isSelected) {
                              if (tempSelected.length > 1) {
                                tempSelected.remove(region);
                              }
                            } else {
                              tempSelected.add(region);
                            }
                          });
                        },
                        child: AnimatedContainer(
                          duration: Duration(milliseconds: 120),
                          padding: EdgeInsets.symmetric(vertical: 8, horizontal: 14),
                          margin: EdgeInsets.symmetric(vertical: 4),
                          decoration: BoxDecoration(
                            color: isSelected ? Colors.blue : Colors.grey[200],
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Text(
                            region,
                            style: TextStyle(
                              color: isSelected ? Colors.white : Colors.black87,
                              fontSize: 15,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      );
                    }).toList(),
                  ),
                ),
              );
            },
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx, null), child: Text('취소')),
            TextButton(onPressed: () => Navigator.pop(ctx, tempSelected), child: Text('확인')),
          ],
        );
      },
    );
    if (result != null && result.isNotEmpty) {
      widget.onEditInterestRegions(result);
    }
  }

  @override
  Widget build(BuildContext context) {
    final sortedMyPosts = [...widget.myPosts]..sort((a, b) => b.date.compareTo(a.date));
    final sortedLikedPosts = [...widget.likedPosts]..sort((a, b) => b.date.compareTo(a.date));

    final postsToShow = _showLiked ? sortedLikedPosts : sortedMyPosts;

    return Scaffold(
      appBar: AppBar(title: Text('프로필')),
      body: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 28),
            GestureDetector(
              onTap: _pickProfileImage,
              child: CircleAvatar(
                radius: 48,
                backgroundColor: Colors.grey[300],
                backgroundImage: widget.profileImage != null ? FileImage(widget.profileImage!) : null,
                child: widget.profileImage == null
                    ? Icon(Icons.person, size: 48, color: Colors.white)
                    : null,
              ),
            ),
            SizedBox(height: 10),
            TextButton.icon(
              onPressed: _pickProfileImage,
              icon: Icon(Icons.camera_alt, size: 18),
              label: Text('프로필 사진 변경'),
            ),
            SizedBox(height: 8),
            _isEditingIntro
                ? Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: Column(
                      children: [
                        TextField(
                          controller: _introController,
                          maxLines: 2,
                          decoration: InputDecoration(
                            hintText: '자기소개를 입력하세요.',
                            border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            TextButton(onPressed: _saveIntro, child: Text('저장')),
                          ],
                        )
                      ],
                    ),
                  )
                : GestureDetector(
                    onTap: _editIntro,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 4.0),
                      child: Row(
                        children: [
                          Expanded(
                            child: Text(
                              widget.intro,
                              style: TextStyle(fontSize: 15, color: Colors.black87),
                            ),
                          ),
                          Icon(Icons.edit, size: 18, color: Colors.blueGrey),
                        ],
                      ),
                    ),
                  ),
            SizedBox(height: 14),
            // 관심지역
            Padding(
              padding: EdgeInsets.only(left: 16, right: 0),
              child: Row(
                children: [
                  GestureDetector(
                    onTap: _editInterestRegions,
                    child: Container(
                      padding: EdgeInsets.all(4),
                      child: Icon(Icons.edit_location_alt, size: 20, color: Colors.blue),
                    ),
                  ),
                  SizedBox(width: 8),
                  Expanded(
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Row(
                        children: widget.interestRegions.map((region) => Container(
                          margin: EdgeInsets.symmetric(horizontal: 4),
                          padding: EdgeInsets.symmetric(vertical: 6, horizontal: 12),
                          decoration: BoxDecoration(
                            color: Colors.blue[50],
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Text(region, style: TextStyle(fontSize: 13)),
                        )).toList(),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(height: 24),
            // ✅ 토글 버튼
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                TextButton(
                  onPressed: () => setState(() => _showLiked = false),
                  child: Text('내 게시글',
                      style: TextStyle(
                        fontWeight: !_showLiked ? FontWeight.bold : FontWeight.normal,
                        color: !_showLiked ? Colors.blue : Colors.black,
                      )),
                ),
                TextButton(
                  onPressed: () => setState(() => _showLiked = true),
                  child: Text('좋아요한 게시글',
                      style: TextStyle(
                        fontWeight: _showLiked ? FontWeight.bold : FontWeight.normal,
                        color: _showLiked ? Colors.blue : Colors.black,
                      )),
                ),
              ],
            ),
            SizedBox(height: 12),
            // ✅ 게시글 리스트
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: postsToShow.isEmpty
                  ? Text(_showLiked ? '좋아요한 게시글이 없습니다.' : '아직 게시글이 없습니다.')
                  : ListView.builder(
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      itemCount: postsToShow.length,
                      itemBuilder: (context, index) {
                        final post = postsToShow[index];
                        return Card(
                          margin: EdgeInsets.only(bottom: 12),
                          child: ListTile(
                            contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                            leading: Image.file(post.image, width: 56, height: 56, fit: BoxFit.cover),
                            title: Text(post.region),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(post.desc),
                                SizedBox(height: 4),
                                Text(
                                  _formatDate(post.date),
                                  style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                                ),
                              ],
                            ),
                            onTap: () {
                              Navigator.of(context).push(
                                MaterialPageRoute(
                                  builder: (_) => PostDetailScreen(
                                    posts: postsToShow,
                                    initialIndex: index,
                                  ),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
            ),
            SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  String _formatDate(DateTime dt) {
    return '${dt.year}.${dt.month.toString().padLeft(2, '0')}.${dt.day.toString().padLeft(2, '0')} '
        '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }
}
