package com.anchorer.lib.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView.OnEditorActionListener;

/**
 * View: EditTextWithClearView
 * 带有清除按钮的EditText，该控件将一个EditText控件与清除按钮（类型为ImageView）绑定在一起，并内置了清除按钮的操作。
 *
 * Created by Anchorer/duruixue on 2013/10/8.
 * @author Anchorer
 */
public class AutoCompleteTextViewWithClearView {
	//包含的控件
	private AutoCompleteTextView editText;
	private ImageView clearView;

	/**
	 * 构造方法
	 * @param editText	输入框
	 * @param clearView	清除内容的按钮
	 */
	public AutoCompleteTextViewWithClearView(AutoCompleteTextView editText, ImageView clearView) {
		this.editText = editText;
		this.clearView = clearView;
		initClearView();
		editText.addTextChangedListener(new EditTextWatcher());
		clearView.setOnClickListener(new ClearContentListener());
	}

	/**
	 * 初始化ClearView的显示
	 */
	private void initClearView() {
		String content = editText.getText().toString();
		if(TextUtils.isEmpty(content))
			clearView.setVisibility(View.GONE);
		else
			clearView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 监听器：监听输入框的内容变化
	 */
	class EditTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String content = s.toString();
			if(TextUtils.isEmpty(content))
				clearView.setVisibility(View.GONE);
			else
				clearView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 监听器：清空输入框中的内容
	 */
	class ClearContentListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			editText.setText("");
			clearView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 获取输入框中的内容
	 */
	public String getText() {
		return editText.getText().toString().trim();
	}
	
	/**
	 * 为输入框设置内容
	 */
	public void setText(String content) {
		editText.setText(content);
	}
	
	/**
	 * 为输入框设置是否可以取得焦点
	 */
	public void setFocusable(boolean focusable) {
		editText.setFocusable(focusable);
	}
	
	/**
	 * 为ClearView设置可见性
	 */
	public void setClearViewVisibility(int visibility) {
		clearView.setVisibility(visibility);
	}
	
	/**
	 * 为输入框设置OnEditorActionListener，以响应不同的Action事件
	 */
	public void setOnEditorActionListener(OnEditorActionListener listener) {
		editText.setOnEditorActionListener(listener);
	}
	
	/**
	 * 为输入框设置TextWatcher，监听输入框的内容变化
	 * @param textWatcher   TextWatcher
	 */
	public void setOnTextChangedListener(TextWatcher textWatcher) {
		editText.addTextChangedListener(textWatcher);
	}
	
	/**
	 * 为输入框设置OnFucusChangeListener，监听焦点变化情况
	 * @param listener  OnFocusChangeListener
	 */
	public void setOnFocusChangeListener(OnFocusChangeListener listener) {
		if(editText != null) {
			editText.setOnFocusChangeListener(listener);
		}
	}
	
	/**
	 * 为输入框设置OnClickListener，监听点击事件
	 * @param listener  OnClickListener
	 */
	public void setOnClickListener(OnClickListener listener) {
		if(editText != null) {
			editText.setOnClickListener(listener);
		}
	}

    /**
     * 为AutoCompleteTextView设置适配器
     * @param adapter   适配器
     */
    public void setAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        if(editText != null) {
            editText.setAdapter(adapter);
        }
    }

    /**
     * 设置触发自动补全的最小字符数
     * @param threshold 字符数
     */
    public void setThreshold(int threshold) {
        if(editText != null ) {
            editText.setThreshold(threshold);
        }
    }

	/**
	 * 获取EditText
	 */
	public AutoCompleteTextView getEditText() {
		return editText;
	}
	
}
