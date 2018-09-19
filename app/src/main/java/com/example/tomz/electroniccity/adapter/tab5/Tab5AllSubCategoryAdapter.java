package com.example.tomz.electroniccity.adapter.tab5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tomz.electroniccity.R;
import com.example.tomz.electroniccity.data.model.tabsubcategory.Tab1AllSubCategoryModel;
import com.example.tomz.electroniccity.data.model.tabsubcategory.Tab5AllSubCategoryModel;
import com.example.tomz.electroniccity.utils.font.CustomTextViewLatoFont;

import java.util.ArrayList;

public class Tab5AllSubCategoryAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<Tab5AllSubCategoryModel> tab5AllSubCategoryModels;

    public Tab5AllSubCategoryAdapter(Context ctx,
                                     ArrayList<Tab5AllSubCategoryModel> tab5AllSubCategoryModels) {
        this.ctx = ctx;
        this.tab5AllSubCategoryModels = tab5AllSubCategoryModels;
    }

    @Override
    public int getCount() {
        return tab5AllSubCategoryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return tab5AllSubCategoryModels.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            view= LayoutInflater.from(ctx).inflate(R.layout.tab5_all_sub_category,viewGroup,false);
        }

        final Tab5AllSubCategoryModel s= (Tab5AllSubCategoryModel) this.getItem(i);

        ImageView img= view.findViewById(R.id.ivImage);
        CustomTextViewLatoFont nameTxt= view.findViewById(R.id.tvTextCat);

        Bitmap bMap = BitmapFactory.decodeResource(ctx.getResources(), s.getImage());
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap,
                (int)(bMap.getWidth()*0.2), (int)(bMap.getHeight()*0.2), true);

        //BIND
        nameTxt.setText(s.getName());
        img.setImageBitmap(bMapScaled);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ctx, s.getName(), Toast.LENGTH_LONG).show();
//               switch (i){
//                   case 0:
//                       break;
//               }
//            }
//        });

        return view;
    }
}
