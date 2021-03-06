package com.example.tomz.electroniccity.page.details.checkout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.Glide;
import com.example.tomz.electroniccity.R;
import com.example.tomz.electroniccity.data.DataManager;
import com.example.tomz.electroniccity.data.local.db.AppDatabase;
import com.example.tomz.electroniccity.data.local.db.AppDbHelper;
import com.example.tomz.electroniccity.data.local.db.dao.CartDao;
import com.example.tomz.electroniccity.data.model.db.CartMdl;
import com.example.tomz.electroniccity.databinding.ProductDetailsBinding;
import com.example.tomz.electroniccity.helper.ToastHelper;
import com.example.tomz.electroniccity.page.details.cart.Cart;
import com.example.tomz.electroniccity.utils.CommonUtils;
import com.example.tomz.electroniccity.utils.base.BaseActivity;
import com.example.tomz.electroniccity.utils.font.CustomTextViewLatoFont;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProductDetail extends BaseActivity<ProductDetailsBinding, ProductDetailViewModel> implements
        ProductDetailNavigator, View.OnTouchListener{

    @Inject DataManager mDataManager;
    @Inject AppDbHelper mAppDbHelper;
    @Inject ProductDetailViewModel mDetailViewModel;
    @Inject AppDatabase mAppDatabase;
    @Inject CartDao mCartDao;
    private ContentLoadingProgressBar mLoadingBar;
    private ImageView mIvProd;
    private LinearLayout mLayoutBtnBuyNow;
    private RelativeLayout mLayoutDiskon;
    private ScrollView mLayoutScrollDetail;
    private ProductDetailsBinding mBinding;
    private PopupWindow popupWindow;
    private String idProd, imgUrl, namaProd, skuProd, hargaProd, spcHargaProd, descProd, longDescProd;
    private CustomTextViewLatoFont mTvNamaProduk, mTvSkuArtikel, mTvNormalPrice,
            mTvSpcPrice, mTvBesarDiskon, mTvSpekProd, mTvDescProd;
    private List<CartMdl> mCartMdlList;
    private int dbSize = 0;

    @Override
    public int getLayoutId() {
        return R.layout.product_details;
    }

    @Override
    public ProductDetailViewModel getViewModel() {
        return mDetailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.detailprod;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getViewDataBinding();
        mDetailViewModel.setNavigator(this);
        setupView();

        mCartMdlList = new ArrayList<>();
        new Handler().postDelayed(() -> {
            mLoadingBar.setVisibility(GONE);
            receiveItemIntent();
        }, 680);

        new Handler().postDelayed(() -> {
            mLayoutScrollDetail.setVisibility(VISIBLE);
            parsingData();
        }, 780);

    }

    private void setupView(){
        Toolbar mToolbar = mBinding.toolbar;
        mTvNamaProduk = mBinding.produkDetail.tvNamaProduk;
        mTvSkuArtikel = mBinding.produkDetail.tvSkuArtikel;
        mTvNormalPrice = mBinding.produkDetail.tvNormalPrice;
        mTvSpcPrice = mBinding.produkDetail.tvSpecialPrice;
        mTvBesarDiskon = mBinding.produkDetail.tvBesarDiskon;
        mTvDescProd = mBinding.produkDetail.tvDesc;
        mTvSpekProd = mBinding.produkDetail.tvSpek;
        mLoadingBar = mBinding.loadDetail;
        mLayoutScrollDetail = mBinding.produkDetail.layoutScrollDetail;
        mIvProd = mBinding.produkDetail.ivImgProduk;
        mLayoutDiskon = mBinding.produkDetail.layoutDiskonDetailPage;

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white),
                    PorterDuff.Mode.SRC_ATOP);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainStatusBarColor();
        }

        mLoadingBar.setVisibility(VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void mainStatusBarColor(){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.newlightblue));
    }

    private void receiveItemIntent(){
        Intent in = getIntent();
        idProd = in.getStringExtra("TAG_ID_PROD");
        imgUrl = in.getStringExtra("TAG_IMG_PROD");
        namaProd = in.getStringExtra("TAG_NAMA_PROD");
        skuProd = in.getStringExtra("TAG_SKU_PROD");
        hargaProd = in.getStringExtra("TAG_NORMAL_PRICE");
        spcHargaProd = in.getStringExtra("TAG_SPC_PRICE");
        descProd = in.getStringExtra("TAG_PROD_DESC");
        longDescProd = in.getStringExtra("TAG_LONG_DESC");
    }

    private void parsingData(){
        Glide.with(this).asBitmap().load(imgUrl).into(mIvProd);
        mTvNamaProduk.setText(namaProd);
        mTvSkuArtikel.setText(skuProd);
        mTvNormalPrice.setText(CommonUtils.setCustomCurrency(hargaProd));
        mTvDescProd.setText(descProd);

        if (CommonUtils.hasHTMLTags(longDescProd)) {
            Log.d("HTML tes1 1", "MASUKKK!!!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("HTML tes1 2", "MASUKKK!!!");
                mTvSpekProd.setText(Html.fromHtml(longDescProd, Html.FROM_HTML_MODE_LEGACY));
            } else {
                Log.d("HTML tes1 3", "MASUKKK!!!");
                mTvSpekProd.setText(Html.fromHtml(longDescProd));
            }
        } else {
            Log.d("no HTML tes1", "MASUKKK!!!");
            mTvSpekProd.setText(longDescProd);
        }
        if(spcHargaProd.isEmpty()){
            mLayoutDiskon.setVisibility(GONE);
            mTvNormalPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            mLayoutDiskon.setVisibility(VISIBLE);
            mTvSpcPrice.setText(spcHargaProd);
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void prepareDataToDatabase(){
        Log.d("prepareDatabase tes1", "MASUKK!!!");
        if (dbSize == 0){
            Log.d("dbSize ToDB", "tes1 " + dbSize);
            CartMdl cartMdl = new CartMdl();
            cartMdl.setId_prod(idProd);
            cartMdl.setImageProduk(imgUrl);
            cartMdl.setNameProduk(namaProd);
            cartMdl.setSkuProduk(skuProd);
            cartMdl.setHargaNormalProduk(hargaProd);
            cartMdl.setHargaPromoProduk(spcHargaProd);
            cartMdl.setDeskripsiProd(descProd);
            cartMdl.setSpeksifikasiProd(longDescProd);
            cartMdl.setQtyItem(1);
            mCartDao.insert(cartMdl);
        } else {
            if (Looper.myLooper() == null) {
                Looper.prepare();
                try {
                    //noinspection LoopStatementThatDoesntLoop
                    for (int idxDB = 0; idxDB < dbSize; idxDB++) {
                        Log.d("dbSize tes1 2", "MASUKK!!!");
                        if (!mCartDao.loadAll().get(idxDB).getId_prod().equals(idProd)) {
                            CartMdl cartMdl = new CartMdl();
                            cartMdl.setId_prod(idProd);
                            cartMdl.setImageProduk(imgUrl);
                            cartMdl.setNameProduk(namaProd);
                            cartMdl.setSkuProduk(skuProd);
                            cartMdl.setHargaNormalProduk(hargaProd);
                            cartMdl.setHargaPromoProduk(spcHargaProd);
                            cartMdl.setDeskripsiProd(descProd);
                            cartMdl.setSpeksifikasiProd(longDescProd);
                            cartMdl.setQtyItem(1);
                            mCartDao.insert(cartMdl);
                        } else {
                            Log.d("dbSize tes1 3", "MASUKK!!!");
                            new Handler(Looper.getMainLooper()).post(() -> ToastHelper
                                    .createToast(ProductDetail.this,
                                            getString(R.string.text_item_in_cart_already), Toast.LENGTH_LONG));
                        }
                    }
                    Looper.myLooper().quit();
                } catch (Exception e) {
                    Log.e("errData2DB tes1", e.getMessage() + "");
                }
            } else {
                Looper.myLooper().quit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPopUpPayOrLater(View view){
        CustomTextViewLatoFont mTvBelanjaLagi, mTvBayar;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = null;
        if (inflater != null) {
            popupView = inflater.inflate(R.layout.popup_shop_or_pay, null);
        }

        if (popupView != null) {
            mTvBelanjaLagi = popupView.findViewById(R.id.tvBelanjaLagi);
            mTvBayar = popupView.findViewById(R.id.tvBayar);
            mTvBelanjaLagi.setOnTouchListener(this);
            mTvBayar.setOnTouchListener(this);
        }

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        Log.i("widthPopUP tes1", width+"");
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        Log.i("heightPopUP tes1", height+"");
        popupWindow = new PopupWindow(popupView, 820, height, true);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
    }

    private static void dimBehind (PopupWindow popupWindow){
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        if (wm != null) {
            wm.updateViewLayout(container, p);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.tvBelanjaLagi:
                view.performClick();
                popupWindow.dismiss();
                finish();
                break;
            case R.id.tvBayar:
                view.performClick();
                Intent in = new Intent(this, Cart.class);
                startActivity(in);
                popupWindow.dismiss();
                new Handler().postDelayed(this::finish, 700);
                break;
        }
        return false;
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onSaveToDatabase() {
        Log.d("onSaveDataB tes1", "MASUKKK!!");
        if (!mDataManager.getUserId().isEmpty()) {
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            setupPopUpPayOrLater(viewGroup);
            new saveDB().execute();
        } else {
            ToastHelper.createToast(this,
                    getString(R.string.text_login_add_to_cart), Toast.LENGTH_LONG);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class saveDB extends AsyncTask<Void,Void,List<CartMdl>>{
        @SuppressWarnings("UnusedAssignment")
        @Override
        protected List<CartMdl> doInBackground(Void... voids) {
            if (mCartDao.countItem() >= 0){
                Log.d("onSaveDB tes1", mCartDao.countItem()+"");
                dbSize = mCartDao.countItem();
                prepareDataToDatabase();
                //noinspection LoopStatementThatDoesntLoop
                for(int idx = 0; idx < mCartDao.countItem(); idx++) {
                    return mCartDao.loadAll();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CartMdl> cartMdlList) {
            berak(cartMdlList);
            mCartMdlList.addAll(cartMdlList);
        }
    }

    private void berak(final List<CartMdl> cartMdl){
        for (int idx = 0; idx < cartMdl.size(); idx++) {
            Log.d("onPostExe tes1", cartMdl.get(idx).getId_prod());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
