## Confirmed Supported Features

The following features are verified by automated tests. The layout of the items marked
**(Word)** is also compared with PDF files exported by Word for Mac: every line has the same
text at the same position (within 1pt).

### Page Layout

- **Page size, orientation, margins and gutter**, per section **(Word)**
- **Sections**: a new page or continuous, each with its own page settings, document grid,
  headers / footers and page numbers **(Word)**
- **Page breaks**: page break characters, "page break before", automatic breaks **(Word)**
- **Keep with next, keep lines together, widow / orphan control** **(Word)**
- Space before a paragraph is ignored at the top of a page, except the first page of a section **(Word)**

### Paragraphs

- **Alignment**: left, center, right, justify, distribute. The last line of a justified paragraph,
  and a line ended by a line break, are not stretched **(Word)**
- **Indents**: left, right, first line, hanging, also in characters **(Word)**
- **Spacing**: before / after (in points and in lines), "don't add space between paragraphs of
  the same style" **(Word)**
- **Line spacing**: single, multiple, at least, exactly, the document grid (line pitch), and
  paragraphs which do not snap to the grid **(Word)**
- **Tabs**: default tab stops, left / center / right tab stops, leaders (dots, hyphens,
  underscores), the hanging indent as a tab stop, positional tabs **(Word)**
- **Borders and shading** of paragraphs **(Word)**

### Line Breaking

- Latin words are not divided; a word longer than a line is **(Word)**
- **Kinsoku**: characters which must not start / end a line, hanging punctuation **(Word)**
- **Compression of punctuation** (`compressPunctuation`): consecutive full-width punctuation, and
  squeezing a character into a justified line **(Word)**
- **Automatic space** between Japanese and Latin text / digits, and turning it off **(Word)**
- In justified English text, spaces shrink to put one more word on a line, as Word does **(Word)**
- Non-breaking spaces and hyphens

### Characters

- **Font size, bold, italic, underline (single / double), strikethrough (single / double),
  color, highlight, shading, superscript / subscript, character spacing, all caps** **(Word)**
- **Fonts per script**: separate fonts for Latin and East Asian text **(Word)**, and theme fonts
- **Bold faces**: a bold face is used only when the font family has one of its own; otherwise the
  regular face is emboldened, advancing 2% of the size more, as Word does **(Word)**
- **Styles**: document defaults, "based on" chains, the default paragraph style, character styles,
  and their precedence over direct formatting **(Word)**
- Hidden text is not output

### Numbering

- **Multi-level numbering** (e.g. `第1条` / `１` / `(1)`), restarting of lower levels, start values
  and their overrides, numbering defined by paragraph styles **(Word)**
- **Number formats**: decimal, full-width, enclosed (circled, parenthesized), roman, letters,
  Japanese counting / legal, aiueo, iroha, bullets **(Word)**
- Suffix (tab / space / nothing) and justification of the number **(Word)**

### Tables

- **Column widths** from the grid, in percent; columns narrower than a long word are widened
  (unless the layout is fixed) **(Word)**
- **Table alignment and indent**, cell margins of the table and of each cell **(Word)**
- **Borders**: of the table and of each cell, none, thick, double; the room borders take **(Word)**
- **Merged cells**: horizontal and vertical **(Word)**
- **Row height** (at least / exactly), **vertical alignment** in cells **(Word)**
- **Rows split across pages**, rows which must not split, **repeated header rows** **(Word)**
- **Table styles**: header row and banded rows **(Word)**
- **Nested tables** **(Word)**

### Headers, Footers and Fields

- **Headers and footers**: default, first page, even pages; inherited from the previous section;
  headers taller than the top margin push the body down **(Word)**
- **Page numbers** (`PAGE`, `NUMPAGES`, `SECTIONPAGES`) in headers, footers and the body, with the
  number formats and start numbers of sections and the format switches of fields **(Word)**
- **Tables of contents and `PAGEREF`**: page numbers recalculated from the PDF layout, links to
  the headings **(Word)**
- **Outline (bookmarks)** of the PDF from the outline levels of headings
- Other fields show the result stored in the Word file

### Pictures

- **Inline pictures** (PNG, JPEG etc.), also in table cells; the height of lines with pictures **(Word)**
- **Floating pictures**, positioned from the page, the margin or the paragraph (the text does not
  wrap around them)
- The same picture used several times is embedded once

### Other

- **Tracked changes**: insertions are output, deletions are not
- **Content controls**, smart tags, hyperlinks
- **Password-protected Word files** and **password-protected output PDF**
- Macro-enabled documents and templates (`.docm`, `.dotx`, `.dotm`): converted like `.docx` files, macros are not run
