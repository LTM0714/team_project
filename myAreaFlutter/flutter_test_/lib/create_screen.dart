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

  // 지역별 대표 좌표 매핑
final Map<String, List<double>> regionCoords = {
  '서울-홍대': [37.5563, 126.9220],
  '서울-이태원': [37.5345, 126.9946],
  '서울-압구정': [37.5271, 127.0286],
  '서울-강남역': [37.4979, 127.0276],
  '서울-명동': [37.5636, 126.9820],
  '서울-건대입구': [37.5400, 127.0703],
  '서울-잠실': [37.5133, 127.1002],
  '서울-신촌': [37.5598, 126.9423],
  '서울-여의도': [37.5219, 126.9189],
  '서울-종로': [37.5729, 126.9794],

  '부산': [35.1796, 129.0756],   // 부산시청
  '대구': [35.8714, 128.6014],   // 대구시청
  '인천': [37.4563, 126.7052],   // 인천시청
  '광주': [35.1595, 126.8526],   // 광주시청
  '대전': [36.3504, 127.3845],   // 대전시청
  '울산': [35.5384, 129.3114],   // 울산시청
  '세종': [36.4800, 127.2890],   // 세종청사

  '경기': [37.4138, 127.5183],   // 경기도청(수원)
  '강원': [37.8228, 128.1555],   // 강원도청(춘천)
  '충북': [36.6357, 127.4913],   // 충북도청(청주)
  '충남': [36.5184, 126.8000],   // 충남도청(홍성)
  '전북': [35.8203, 127.1088],   // 전북도청(전주)
  '전남': [34.8161, 126.4631],   // 전남도청(무안)
  '경북': [36.5760, 128.5056],   // 경북도청(안동)
  '경남': [35.2383, 128.6924],   // 경남도청(창원)
  '제주': [33.4996, 126.5312],   // 제주시청
};


  Future<void> _pickImage(ImageSource source) async {
    final pickedFile = await ImagePicker().pickImage(source: source);
    if (pickedFile != null) {
      setState(() => _imageFile = File(pickedFile.path));
    }
  }

  void _upload() {
    if (_imageFile == null || _selectedRegion == null || _descController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('사진, 지역, 설명을 모두 입력해주세요.')),
      );
      return;
    }

    // 선택된 지역에 대한 대표 좌표 가져오기 (없으면 서울시청 좌표 기본값)
    final coords = regionCoords[_selectedRegion] ?? [37.5665, 126.9780];

    final post = Post(
      image: _imageFile!,
      region: _selectedRegion!,
      desc: _descController.text,
      date: DateTime.now(),
      latitude: coords[0],
      longitude: coords[1],
    );

    widget.onUpload(post);

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('업로드 완료!')),
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
      appBar: AppBar(title: const Text('사진 업로드')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _imageFile != null
                ? Container(
                    height: 200,
                    margin: const EdgeInsets.only(bottom: 16),
                    child: Image.file(_imageFile!, fit: BoxFit.cover),
                  )
                : SizedBox(height: 200),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.camera),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      minimumSize: const Size(0, 56),
                    ),
                    child: const Text('사진 찍기', style: TextStyle(fontSize: 18)),
                  ),
                ),
                const SizedBox(width: 20),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.gallery),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      minimumSize: const Size(0, 56),
                    ),
                    child: const Text('갤러리에서 선택', style: TextStyle(fontSize: 18)),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            const Text('지역', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            DropdownButtonFormField<String>(
              value: _selectedRegion,
              items: widget.regions
                  .map((region) => DropdownMenuItem(value: region, child: Text(region)))
                  .toList(),
              decoration: InputDecoration(
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
              ),
              hint: const Text('지역을 선택하세요'),
              onChanged: (value) {
                setState(() => _selectedRegion = value);
              },
            ),
            const SizedBox(height: 24),
            const Text('설명', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 8),
            TextField(
              controller: _descController,
              minLines: 3,
              maxLines: 5,
              decoration: InputDecoration(
                hintText: '이 장소에 대해 설명해주세요',
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: _upload,
              style: ElevatedButton.styleFrom(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(20),
                ),
                minimumSize: const Size(0, 56),
              ),
              child: const Text('업로드하기', style: TextStyle(fontSize: 18)),
            ),
          ],
        ),
      ),
    );
  }
}
