import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DTextModel extends DShapeModel{
	private String text;
    private String fontName;
    private int fontStyle;
    private int fontSize;
    private int index = 50;
	public DTextModel() {
		super();
	}
	
	@Override
    public void mimic(DShapeModel other) {
        super.mimic(other);
        this.text = ((DTextModel)other).getText();
        this.fontName = ((DTextModel)other).getFontName();
        this.fontStyle = ((DTextModel)other).getFontStyle();
        this.index = ((DTextModel)other).getIndex();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.notifyModelChanged();
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        this.notifyModelChanged();
    }

    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int i) {
        this.index = i;
    }
}
