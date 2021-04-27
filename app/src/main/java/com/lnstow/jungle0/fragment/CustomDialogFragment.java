package com.lnstow.jungle0.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.lnstow.jungle0.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private byte dialogType;
    private int checkedItem;
    private int maxPage;

    private OnFragmentInteractionListener mListener;
    public static final byte DIALOG_PEOPLE_NUM = 1;
    public static final byte DIALOG_SEARCH = 2;
    public static final byte DIALOG_VIEW_SIZE = 3;
    public static final byte DIALOG_PAGE = 4;
    public static final byte DIALOG_FAVORITE = 5;
    public static final byte DIALOG_FIRST_USE = 6;

    public CustomDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogType  Parameter 1.
     * @param checkedItem
     * @return A new instance of fragment CustomDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomDialogFragment newInstance(byte dialogType, int checkedItem, int maxPage) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putByte(ARG_PARAM1, dialogType);
        args.putInt(ARG_PARAM2, checkedItem);
        args.putInt(ARG_PARAM3, maxPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogType = getArguments().getByte(ARG_PARAM1);
            checkedItem = getArguments().getInt(ARG_PARAM2);
            maxPage = getArguments().getInt(ARG_PARAM3);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources resources = getResources();
        switch (dialogType) {
            case DIALOG_PEOPLE_NUM:
                builder.setTitle(resources.getString(R.string.actress_num));
                builder.setSingleChoiceItems(resources.getStringArray(R.array.people_num_show),
                        checkedItem, (dialog, which) -> {
                            if (which != checkedItem)
                                mListener.dialogNotifyChange(dialogType, which, null);
                            dialog.dismiss();
                        });
                break;
            case DIALOG_SEARCH:
                builder.setTitle(resources.getString(R.string.search_action));
                EditText editText = new EditText(getActivity());
                editText.setHint(R.string.search_hint);
                editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                builder.setView(editText);
                builder.setPositiveButton(resources.getString(R.string.search_action)
                        , (dialog, which) -> {
                            String value = editText.getText().toString();
                            if (!value.isEmpty())
                                mListener.dialogNotifyChange(dialogType, 0, value);
                            if (imm != null)
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            dialog.dismiss();
                        });
                editText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        String value = v.getText().toString();
                        if (!value.isEmpty())
                            mListener.dialogNotifyChange(dialogType, 0, value);
                        if (imm != null)
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dismiss();
                        return true;
                    }
                    return false;
                });
//                editText.setFocusable(true);
//                editText.setFocusableInTouchMode(true);
//                ((View)editText).setFocusable(true);
//                ((View)editText).setFocusableInTouchMode(true);
//                editText.requestFocus();
//                editText.requestFocusFromTouch();
                editText.postDelayed(() -> {
                    if (imm != null)
                        imm.showSoftInput(editText, 0);
                }, 100);
                break;
            case DIALOG_VIEW_SIZE:
                builder.setMessage("asd");
                break;
            case DIALOG_PAGE:
                builder.setTitle(resources.getString(R.string.page));
                int showMaxPage = maxPage / 100 + 1;
                String[] show = new String[showMaxPage];
                for (int i = 0; i < showMaxPage; i++)
                    show[i] = Integer.toString(i);
                builder.setSingleChoiceItems(show, checkedItem / 100, (dialog, which) -> {
                    if (which != checkedItem / 100)
                        mListener.dialogNotifyChange(dialogType, which, null);
                    dialog.dismiss();
                });
//                builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                if (showMaxPage > 5) {
                    Space space = new Space(getActivity());
                    builder.setView(space);
                    AlertDialog dialog = builder.create();
                    Point point = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(point);
                    space.postDelayed(() -> {
                                if (point.x < point.y)
                                    dialog.getWindow().setLayout(
                                            point.x * 2 / 3, point.y * 2 / 3);
                            }
                            , 100);
                    return dialog;
                }
                break;
            default:
                break;
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument dialogType and name
        void dialogNotifyChange(byte dialogType, int index, String textValue);
    }
}
