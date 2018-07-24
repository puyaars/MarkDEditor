package xute.markdeditor.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import xute.markdeditor.R;
import xute.markdeditor.models.ComponentTag;
import xute.markdeditor.models.TextComponentModel;
import xute.markdeditor.utilities.FontSize;

import static xute.markdeditor.Styles.TextComponentStyle.BLOCKQUOTE;
import static xute.markdeditor.Styles.TextComponentStyle.H1;
import static xute.markdeditor.Styles.TextComponentStyle.H5;
import static xute.markdeditor.Styles.TextComponentStyle.NORMAL;

public class TextComponent {
  private final Context mContext;
  private TextComponentCallback _textComponentCallback;
  private Resources r;

  public TextComponent(Context context, TextComponentCallback textComponentCallback) {
    this.mContext = context;
    r = mContext.getResources();
    _textComponentCallback = textComponentCallback;
  }

  /**
   * Method to create new instance according to mode provided.
   * Mode can be [PLAIN, UL, OL]
   * @param mode mode of new TextComponent.
   * @return new instance of TextComponent.
   */
  public TextComponentItem newTextComponent(final int mode) {
    final TextComponentItem customInput = new TextComponentItem(mContext, mode);
    final EditText et = customInput.getInputBox();
    et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);
    et.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
          return true;
        if (keyCode == KeyEvent.KEYCODE_DEL) {
          if (_textComponentCallback != null) {
            _textComponentCallback.onRemoveTextComponent(((ComponentTag) customInput.getTag()).getComponentIndex());
          }
        }
        return false;
      }
    });

    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean inFocus) {
        if (inFocus) {
          if (_textComponentCallback != null) {
            _textComponentCallback.onFocusGained(customInput);
          }
        }
      }
    });

    et.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.length() > 1) {
          char ch = charSequence.charAt(charSequence.length() - 1);
          if (ch == '\n') {
            et.setText(charSequence.subSequence(0, charSequence.length() - 1));
            if (_textComponentCallback != null) {
              _textComponentCallback.onInsertTextComponent(((ComponentTag) customInput.getTag()).getComponentIndex());
            }
          }
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });
    return customInput;
  }

  /**
   * updates view with latest style info.
   * @param view to be updated.
   */
  public void updateComponent(View view) {
    ComponentTag componentTag = (ComponentTag) view.getTag();
    //check heading
    int style = ((TextComponentModel) componentTag.getComponent()).getHeadingStyle();
    ((TextComponentItem) view).getInputBox().setTextSize(FontSize.getFontSize(style));

    if (style >= H1 && style <= H5) {
      ((TextComponentItem) view).getInputBox().setTypeface(null, Typeface.BOLD);
      (((TextComponentItem) view).getInputBox()).setBackgroundResource(R.drawable.text_input_bg);
      ((TextComponentItem) view).getInputBox().setPadding(
       dpToPx(12),//left
       dpToPx(8),//top
       dpToPx(12),//right
       dpToPx(8)//bottom
      );
      ((TextComponentItem) view).getInputBox().setLineSpacing(2f,1.1f);
    }

    if (style == NORMAL) {
      ((TextComponentItem) view).getInputBox().setTypeface(null, Typeface.NORMAL);
      (((TextComponentItem) view).getInputBox()).setBackgroundResource(R.drawable.text_input_bg);
      ((TextComponentItem) view).getInputBox().setPadding(
       dpToPx(12),//left
       dpToPx(4),//top
       dpToPx(12),//right
       dpToPx(4)//bottom
      );
      ((TextComponentItem) view).getInputBox().setLineSpacing(2f,1.1f);
    }

    if (style == BLOCKQUOTE) {
      ((TextComponentItem) view).getInputBox().setTypeface(null, Typeface.ITALIC);
      (((TextComponentItem) view).getInputBox()).setBackgroundResource(R.drawable.blockquote_component_bg);
      ((TextComponentItem) view).getInputBox().setPadding(
       dpToPx(18),//left
       dpToPx(16),//top
       dpToPx(18),//right
       dpToPx(16)//bottom
      );
      ((TextComponentItem) view).getInputBox().setLineSpacing(2f,1.2f);
    }
  }

  /**
   * Convert dp to px value.
   * @param dp value
   * @return pixel value of given dp.
   */
  private int dpToPx(int dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, r.getDisplayMetrics());
  }

  public interface TextComponentCallback {
    void onInsertTextComponent(int selfIndex);

    void onFocusGained(View view);

    void onRemoveTextComponent(int selfIndex);
  }
}
