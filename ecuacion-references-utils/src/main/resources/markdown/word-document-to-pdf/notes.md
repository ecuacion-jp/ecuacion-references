### Not Supported

The following are not output. When a document has them, they are skipped (a warning is logged
for drawings such as shapes and charts), and the rest of the document is output.

- Shapes, text boxes, charts, SmartArt, equations
- Footnotes and endnotes, comments
- Multiple columns, vertical writing
- Text wrapping around floating pictures
- Small caps, character scaling (`w:w`), raised / lowered position (`w:position`), emphasis marks
- EMF / WMF pictures
- The legacy `.doc` format: an exception is thrown (save it as `.docx` first)

### Layout Compared with Word for Mac

The layout follows PDF files exported by **Word for Mac**. Word for Windows lays out some fonts
(e.g. Yu Mincho of 11pt or larger on the document grid) with larger line heights, so lines per
page and page breaks can differ from a PDF exported by Word for Windows.

To get the same layout as Word, the fonts the document uses must be installed on the server and
used with `builderForSystemFonts()`. With other fonts, the widths of the characters differ, and so
do where lines and pages break.

Other known differences from Word:

- Italic text is the regular face slanted (Word uses an italic face when the font has one).
- In justified English text, Word also narrows the spaces between characters slightly. This
  library narrows only the spaces between words: lines break at the same places, but the
  positions in a line can differ by less than 1pt.
- Documents in the compatibility mode of Word 2010 or earlier have not been compared with Word
  (the position of tables and the document grid in table cells may differ).

### Handling Large Files

The whole Word file is loaded into memory. When processing files uploaded by users, check the
file size before calling this library:

```java
if (Files.size(wordPath) > 50 * 1024 * 1024) { // e.g. 50 MB
    throw new IllegalArgumentException("File size exceeds the allowed limit");
}
WordToPdfUtil.generate(wordPath, outputPath, options);
```
