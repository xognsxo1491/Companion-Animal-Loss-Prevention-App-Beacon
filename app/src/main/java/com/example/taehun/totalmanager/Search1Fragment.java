package com.example.taehun.totalmanager;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.Search1Request;

import org.json.JSONException;
import org.json.JSONObject;

public class Search1Fragment extends Fragment {

    public Search1Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search1, container, false);

        final EditText edit_search_name = (EditText) view.findViewById(R.id.edit_search_name);
        final EditText edit_search_email = (EditText) view.findViewById(R.id.edit_search_email);
        final TextView textView = (TextView) view.findViewById(R.id.text_search_id);

        Button btn_search_id = (Button) view.findViewById(R.id.btn_search_id);

        btn_search_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = edit_search_name.getText().toString();
                final String useremail = edit_search_email.getText().toString();

                if (username.equals("")) { // 아이디 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("아이디 입력란이 공백입니다.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                }

                else if (useremail.equals("")) { // 이메일 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("이메일 입력란이 공백입니다.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                }

                else { //마찬가지로 성공적일경우 success 문구 나옴
                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    String userid = jsonObject.getString("Id");
                                    textView.setText(userid);

                                    ConstraintLayout layout = (ConstraintLayout)view.findViewById(R.id.layout_message);
                                    layout.setVisibility(View.VISIBLE);

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("입력정보가 일치하지 않습니다.")
                                            .setNegativeButton("재입력", null)
                                            .create()
                                            .show();
                                    edit_search_name.setText("");
                                    edit_search_email.setText("");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Search1Request search1Request = new Search1Request(username, useremail, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    queue.add(search1Request);
                }
            }
        });
        return view;
    }
}
