import olefile
import struct
import re
import sys

# Force UTF-8 output
sys.stdout.reconfigure(encoding='utf-8')

file_path = r'D:\AAAend\lllen\Web开发\物联网工程应用期末作品、考核形式、设计文档模板(2026).doc'
ole = olefile.OleFileIO(file_path)

print("=== OLE Streams ===")
for stream in ole.listdir():
    print('/'.join(stream))

# Method 1: Extract all readable text from the raw file content
# Read the entire OLE file to find text segments
print("\n=== Method: Read raw file as bytes, extract text ===")
with open(file_path, 'rb') as f:
    raw = f.read()

# Try to decode as UTF-16LE chunks
# Look for sequences of valid UTF-16LE characters
text_parts = []
i = 0
while i < len(raw) - 1:
    char_code = raw[i] | (raw[i+1] << 8)
    # Check for Chinese characters, ASCII printable, and common punctuation
    is_valid = (
        (0x4e00 <= char_code <= 0x9fff) or  # CJK Unified
        (0x3400 <= char_code <= 0x4dbf) or  # CJK Extension A
        (0x3000 <= char_code <= 0x303f) or  # CJK punctuation
        (0xff00 <= char_code <= 0xffef) or  # Fullwidth forms
        (0x2000 <= char_code <= 0x206f) or  # General punctuation
        (0x000a <= char_code <= 0x000d) or  # Line breaks
        (0x0020 <= char_code <= 0x007e) or  # ASCII printable
        (0x4e00 <= char_code <= 0x9fff)
    )
    if is_valid:
        text_parts.append(chr(char_code))
    else:
        text_parts.append('@')
    i += 2

text = ''.join(text_parts)
# Collapse multiple non-text chars
text = re.sub(r'@{3,}', '\n', text)
text = re.sub(r'@{1,2}', '', text)

# Print lines that seem meaningful (at least 5 Chinese chars or 20 chars total)
lines = text.split('\n')
for line in lines:
    line = line.strip()
    cn_chars = sum(1 for c in line if '一' <= c <= '鿿')
    if cn_chars >= 3 or len(line) >= 15:
        print(line)

ole.close()
