import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';

import 'post_model.dart';

class CreateScreen extends StatefulWidget {
  final Function(Post) onUpload;
  final List<String> regions;
  const CreateScreen({required this.onUpload, required this.regions, super.key});

  @override
  State<CreateScreen> createState() => _CreateScreenState();
}

class _CreateScreenState extends State<CreateScreen> {
  File? _imageFile;
  String? _selectedRegion;
  final TextEditingController _descController = TextEditingController();

  Future<void> _pickImage(ImageSource source) async {
    final pickedFile = await ImagePicker().pickImage(source: source);
    if (pickedFile != null) {
      setState(() => _imageFile = File(pickedFile.path));
    }
  }

  void _upload() {
    if (_imageFile == null || _selectedRegion == null || _descController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('사진, 지역, 설명을 모두 입력해주세요.')),
      );
      return;
    }
    final post = Post(
      image: _imageFile!,
      region: _selectedRegion!,
      desc: _descController.text,
      date: DateTime.now(),
    );
    widget.onUpload(post);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('업로드 완료!')),
    );
    setState(() {
      _imageFile = null;
      _selectedRegion = null;
      _descController.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('사진 업로드')),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _imageFile != null
                ? Container(
                    height: 200,
                    margin: EdgeInsets.only(bottom: 16),
                    child: Image.file(_imageFile!, fit: BoxFit.cover),
                  )
                : Container(
                    height: 200,
                    margin: EdgeInsets.only(bottom: 16),
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey, width: 2),
                      borderRadius: BorderRadius.circular(12),
                      color: Colors.grey[100],
                    ),
                    child: Center(
                      child: Icon(
                        Icons.image_outlined, // ✅ 액자+산 아이콘
                        size: 120,             // ✅ 크게 꽉 차게 보이도록
                        color: Colors.grey[400],
                      ),
                    ),
                  ),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.camera),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      minimumSize: Size(0, 56),
                    ),
                    child: Text('사진 찍기', style: TextStyle(fontSize: 18)),
                  ),
                ),
                SizedBox(width: 20),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.gallery),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      minimumSize: Size(0, 56),
                    ),
                    child: Text('갤러리에서 선택', style: TextStyle(fontSize: 18)),
                  ),
                ),
              ],
            ),
            SizedBox(height: 24),
            Text('지역', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            DropdownButtonFormField<String>(
              value: _selectedRegion,
              items: widget.regions
                  .map((region) => DropdownMenuItem(value: region, child: Text(region)))
                  .toList(),
              decoration: InputDecoration(
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
              ),
              hint: Text('지역을 선택하세요'),
              onChanged: (value) {
                setState(() => _selectedRegion = value);
              },
            ),
            SizedBox(height: 24),
            Text('설명', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            SizedBox(height: 8),
            TextField(
              controller: _descController,
              minLines: 3,
              maxLines: 5,
              decoration: InputDecoration(
                hintText: '이 장소에 대해 설명해주세요',
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
            SizedBox(height: 32),
            ElevatedButton(
              onPressed: _upload,
              style: ElevatedButton.styleFrom(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(20),
                ),
                minimumSize: Size(0, 56),
              ),
              child: Text('업로드하기', style: TextStyle(fontSize: 18)),
            ),
          ],
        ),
      ),
    );
  }
}
