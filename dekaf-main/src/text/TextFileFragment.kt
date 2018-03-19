package org.jetbrains.dekaf.text


/**
 * @author Leonid Bushuev
 */
class TextFileFragment : TextFragment {

    override val textName: String

    val fragmentName: String?


    /**
     * Instantiates a new TextFragment.
     *
     * @param text          text fragment itself.
     * @param fileName      the name of file this fragment from.
     * @param row           the row number, starts with 1.
     * @param pos           the number of the position inside the row, starts with 1.
     * @param fragmentName  optional name of the fragment.
     */
    constructor(text: String,
                fileName: String,
                row: Int,
                pos: Int,
                fragmentName: String?) : super(text, row, pos) {
        textName = fileName
        this.fragmentName = fragmentName
    }

    override val textLocationHumanReadable: String
        get() {
            var location = "Fragment at $textName:$row:$pos"
            if (fragmentName != null) location += " ($fragmentName)"
            return location
        }

}
