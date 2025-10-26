import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'post_model.dart';
import 'post_detail_screen.dart';

class ProfileScreen extends StatefulWidget {
  final List<Post> myPosts;
  final List<Post> likedPosts;
  final File? profileImage;
  final void Function(File?) onEditProfileImage;
  final String intro;
  final void Function(String) onEditIntro;
  final List<String> interestRegions;
  final void Function(List<String>) onEditInterestRegions;
  final List<String> regions;

  const ProfileScreen({
    required this.myPosts,
    required this.likedPosts,
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
  bool _showLiked = false;

  // 로컬 복제본: 외부에서 전달된 리스트를 직접 변경하지 않도록 로컬에서 관리
  late List<Post> myPostsLocal;
  late List<Post> likedPostsLocal;

  // 마지막 탭의 롱프레스 위치(글로벌 좌표) — showMenu에 사용
  Offset? _tapPosition;

  @override
  void initState() {
    super.initState();
    myPostsLocal = List.from(widget.myPosts);
    likedPostsLocal = List.from(widget.likedPosts);
  }

  void _storeTapPosition(TapDownDetails details) {
    _tapPosition = details.globalPosition;
  }

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
          title: const Text('관심지역 선택'),
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
                          duration: const Duration(milliseconds: 120),
                          padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 14),
                          margin: const EdgeInsets.symmetric(vertical: 4),
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
            TextButton(onPressed: () => Navigator.pop(ctx, null), child: const Text('취소')),
            TextButton(onPressed: () => Navigator.pop(ctx, tempSelected), child: const Text('확인')),
          ],
        );
      },
    );
    if (result != null && result.isNotEmpty) {
      widget.onEditInterestRegions(result);
    }
  }

  Future<void> _confirmAndDeletePost({required Post post, required bool fromLiked}) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('삭제 확인'),
        content: const Text('삭제하시겠습니까?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('아니오')),
          TextButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('예')),
        ],
      ),
    );

    if (ok == true) {
      setState(() {
        // 삭제: 내 게시글에서만 삭제할 경우 myPostsLocal에서 제거
        myPostsLocal.removeWhere((p) => identical(p, post) || p == post);
        // 좋아요 목록에서도 제거
        likedPostsLocal.removeWhere((p) => identical(p, post) || p == post);
      });
    }
  }

  // 롱프레스 시 해당 위치에 작은 메뉴(삭제)를 띄움
  Future<void> _showLongPressMenu({required Post post, required bool fromLiked}) async {
    if (_tapPosition == null) return;

    final RenderBox overlay = Overlay.of(context).context.findRenderObject() as RenderBox;
    final result = await showMenu<String>(
      context: context,
      position: RelativeRect.fromRect(
        Rect.fromPoints(_tapPosition!, _tapPosition!),
        Offset.zero & overlay.size,
      ),
      items: [
        PopupMenuItem<String>(
          value: 'delete',
          child: Container(
            width: 120,
            height: 36,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(6),
            ),
            child: const Text('삭제', style: TextStyle(color: Colors.red)),
          ),
        ),
      ],
      elevation: 2,
    );

    if (result == 'delete') {
      await _confirmAndDeletePost(post: post, fromLiked: fromLiked);
    }
  }

  @override
  Widget build(BuildContext context) {
    // 정렬: 최신순
    final sortedMyPosts = [...myPostsLocal]..sort((a, b) => b.date.compareTo(a.date));
    final sortedLikedPosts = [...likedPostsLocal]..sort((a, b) => b.date.compareTo(a.date));
    final postsToShow = _showLiked ? sortedLikedPosts : sortedMyPosts;

    return Scaffold(
      appBar: AppBar(
        title: const Text('프로필'),
        actions: [
          // 케밥 메뉴: 드롭다운으로 '로그아웃' 항목(사각형 스타일)만 표시, 기능 없음
          PopupMenuButton<int>(
            padding: const EdgeInsets.only(right: 8),
            icon: const Icon(Icons.more_vert),
            itemBuilder: (context) => [
              PopupMenuItem<int>(
                value: 0,
                // 사각형으로 보이는 로그아웃 "버튼" 스타일
                child: Container(
                  width: 140,
                  height: 44,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(6),
                    color: Colors.white,
                  ),
                  child: const Text('로그아웃', style: TextStyle(color: Colors.black87)),
                ),
              ),
            ],
            onSelected: (value) {
              // 기능 없음: 드롭다운만 보여주기
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(height: 28),
            GestureDetector(
              onTap: _pickProfileImage,
              child: CircleAvatar(
                radius: 48,
                backgroundColor: Colors.grey[300],
                backgroundImage: widget.profileImage != null ? FileImage(widget.profileImage!) : null,
                child: widget.profileImage == null
                    ? const Icon(Icons.person, size: 48, color: Colors.white)
                    : null,
              ),
            ),
            const SizedBox(height: 10),
            TextButton.icon(
              onPressed: _pickProfileImage,
              icon: const Icon(Icons.camera_alt, size: 18),
              label: const Text('프로필 사진 변경'),
            ),
            const SizedBox(height: 8),
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
                            TextButton(onPressed: _saveIntro, child: const Text('저장')),
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
                              style: const TextStyle(fontSize: 15, color: Colors.black87),
                            ),
                          ),
                          const Icon(Icons.edit, size: 18, color: Colors.blueGrey),
                        ],
                      ),
                    ),
                  ),
            const SizedBox(height: 14),
            // 관심지역
            Padding(
              padding: const EdgeInsets.only(left: 16, right: 0),
              child: Row(
                children: [
                  GestureDetector(
                    onTap: _editInterestRegions,
                    child: Container(
                      padding: const EdgeInsets.all(4),
                      child: const Icon(Icons.edit_location_alt, size: 20, color: Colors.blue),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Row(
                        children: widget.interestRegions
                            .map((region) => Container(
                                  margin: const EdgeInsets.symmetric(horizontal: 4),
                                  padding: const EdgeInsets.symmetric(vertical: 6, horizontal: 12),
                                  decoration: BoxDecoration(
                                    color: Colors.blue[50],
                                    borderRadius: BorderRadius.circular(16),
                                  ),
                                  child: Text(region, style: const TextStyle(fontSize: 13)),
                                ))
                            .toList(),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            // 토글 버튼
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
            const SizedBox(height: 12),
            // 게시글 리스트
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: postsToShow.isEmpty
                  ? Text(_showLiked ? '좋아요한 게시글이 없습니다.' : '아직 게시글이 없습니다.')
                  : ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: postsToShow.length,
                      itemBuilder: (context, index) {
                        final post = postsToShow[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 12),
                          child: GestureDetector(
                            onTapDown: _storeTapPosition, // 롱프레스 메뉴 위치 저장
                            onLongPress: () async {
                              await _showLongPressMenu(post: post, fromLiked: _showLiked);
                            },
                            child: ListTile(
                              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                              leading: Image.file(post.image, width: 56, height: 56, fit: BoxFit.cover),
                              title: Text(post.region),
                              subtitle: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(post.desc),
                                  const SizedBox(height: 4),
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
                          ),
                        );
                      },
                    ),
            ),
            const SizedBox(height: 24),
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
