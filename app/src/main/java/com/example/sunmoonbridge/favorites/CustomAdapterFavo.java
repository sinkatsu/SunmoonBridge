package com.example.sunmoonbridge.favorites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;

import com.example.sunmoonbridge.Post;
import com.example.sunmoonbridge.PostDetail;
import com.example.sunmoonbridge.R;
import com.example.sunmoonbridge.StudentMembars;
import com.example.sunmoonbridge.ui.Help.CustomAdapter;
import com.example.sunmoonbridge.ui.Help.PicassoSampleActivity;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CustomAdapterFavo extends ArrayAdapter implements AdapterView.OnItemClickListener {

    private Context context;
    private ArrayList<Post> list;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference root = firebaseDatabase.getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("PostData");
    int favo;
    private ArrayList<String> favolist = new ArrayList<>();
    boolean j = false;
    boolean fvchecker = false;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String UID = firebaseAuth.getUid();
    StudentMembars sm;
    String nickname;
    String wPost;
    ArrayList<String> types = new ArrayList<>();

    public CustomAdapterFavo(Context context, ArrayList<Post> list, ArrayList<String> types) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.types = types;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent it= new Intent(context, PostDetail.class);
        it.putExtra("detail",(Post)list.get(i));
        it.putExtra("Userid",UID);
        it.putExtra("nickname",nickname);
        context.startActivity(it);
    }

    public class ViewHolder {
        public TextView tv_name;
        public TextView tv_title;
        public TextView tv_date;
        public TextView tv_note;
        public ImageView im1;
        public ImageView im2;
        public ImageView im3;
        public ImageView im4;
        public ImageView im5;
        public MaterialFavoriteButton MFB;
        public LinearLayout linearLayout;
        public TextView complete;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){//여기서 view를 사용하면 이미지 처리가 잘 안된다.
        final CustomAdapterFavo.ViewHolder viewHolder;
        View convertView;//그래서 새로 view를 생성해야한다.


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        convertView = layoutInflater.inflate(R.layout.post_item, parent, false);


        viewHolder = new CustomAdapterFavo.ViewHolder();
        viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name1);
        viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title1);
        viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date1);
        viewHolder.tv_note = (TextView) convertView.findViewById(R.id.tv_note1);

        viewHolder.im1 = (ImageView) convertView.findViewById(R.id.iv_post1);
        viewHolder.im2 = (ImageView) convertView.findViewById(R.id.iv_post2);
        viewHolder.im3 = (ImageView) convertView.findViewById(R.id.iv_post3);
        viewHolder.im4 = (ImageView) convertView.findViewById(R.id.iv_post4);
        viewHolder.im5 = (ImageView) convertView.findViewById(R.id.iv_post5);
        viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayoutPost1);
        viewHolder.complete= (TextView) convertView.findViewById(R.id.completecheck2);

        //dbでデータをクラス型で抽出したクラスに入った値を受け取りアダプターに値をセットする
        final Post post = (Post) list.get(position);
        viewHolder.tv_name.setText(post.getNickname());
        viewHolder.tv_title.setText(post.getTitle());
        viewHolder.tv_note.setText(post.getNote());
        viewHolder.tv_date.setText(post.getDate());
        if(post.getCompletecheck()==1){
            viewHolder.complete.setVisibility(View.VISIBLE);

            if(types.get(position).contains("Knowledge")){
                viewHolder.complete.setText("해결");
            }else if(types.get(position).contains("Trade")){
                viewHolder.complete.setText("거래종료");
            }else viewHolder.complete.setText("접수종료");

        }else {
            viewHolder.complete.setBackgroundResource(R.drawable.frame_style);
            if(types.get(position).contains("Knowledge")){
                viewHolder.complete.setText("미해결");
            }else if(types.get(position).contains("Trade")){
                viewHolder.complete.setText("거래접수중");
            }else viewHolder.complete.setText("접수중");
        }

        if (post.getDivision().equals("take")) {
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FFD7D7"));
        } else if (post.getDivision().equals("give")){
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#D5FBFF"));
        } else if (post.getDivision().equals("knowledge")){
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        /////////////////////////////////////////////////////////////////////////////////// 줄겨찾기
        viewHolder.MFB = convertView.findViewById(R.id.favbt);

        //vxhusijfk232sdhdk는 ID명 나준에 수정
        DatabaseReference node = databaseReference.child(UID).child("favorite");

        final int postkey = position;
        //favorite 상태출력 초기화
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(post.getPostid())) {
                    viewHolder.MFB.setFavoriteAnimated(true);
                } else {
                    viewHolder.MFB.setFavorite(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //post의 favorite 수을 갠신하고 favorite상태를 접수하는 부분

        viewHolder.MFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fvchecker = true;

                node.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (fvchecker == true) {
                            if (snapshot.hasChild(post.getPostid())) {
                                //Toast.makeText(getContext(),"fgsf",Toast.LENGTH_SHORT).show();
                                node.child(post.getPostid()).removeValue();
                                favo = post.getFavorite();
                                favo -= 1;
                                databaseReference2.child(types.get(position)).child(post.getPostid()).child("postdetail").child("favorite").setValue(favo);
                                fvchecker = false;
                            } else {
                                //Toast.makeText(getContext(), "eeee", Toast.LENGTH_SHORT).show();
                                favo = post.getFavorite();
                                //favo += 1;
                                //databaseReference2.child(types.get(position)).child(post.getPostid()).child("postdetail").child("favorite").setValue(favo);
                                //추가
                                node.child(post.getPostid()).setValue(true);
                                fvchecker = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });


        ArrayList<String> urls = new ArrayList<>();
        if (post.getDlUrlsingle() != null) {
            String s = post.getDlUrlsingle();
            try {
                StringTokenizer st = new StringTokenizer(s, "***");
                for (int j = 0; j < post.getPhotonum(); j++) {
                    urls.add(st.nextToken());
                }
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), "e " + " e", Toast.LENGTH_SHORT).show();

            }
        }

        CustomAdapterFavo.RoundedTransformation transform = new CustomAdapterFavo.RoundedTransformation(50, 10);//아래에서 정의한 클래스의 겍체를 생성
        CustomAdapterFavo.RoundedTransformation transform2 = new CustomAdapterFavo.RoundedTransformation(20, 10);

        if (post.getPhotonum() >= 5) {
            Picasso.get().load(urls.get(0)).resize(540, 540).centerCrop().into(viewHolder.im1);
            Picasso.get().load(urls.get(1)).resize(540, 540).centerCrop().into(viewHolder.im2);
            Picasso.get().load(urls.get(2)).resize(200, 200).centerCrop().into(viewHolder.im3);
            Picasso.get().load(urls.get(3)).resize(200, 200).centerCrop().into(viewHolder.im4);
            Picasso.get().load(urls.get(4)).resize(200, 200).centerCrop().into(viewHolder.im5);

            Picasso.get().load(urls.get(0)).resize(540, 540).
                    centerCrop().transform(transform).into(viewHolder.im1);
            Picasso.get().load(urls.get(1)).resize(540, 540).
                    centerCrop().transform(transform).into(viewHolder.im2);
            Picasso.get().load(urls.get(2)).resize(200, 200).
                    centerCrop().transform(transform2).into(viewHolder.im3);
            Picasso.get().load(urls.get(3)).resize(200, 200).
                    centerCrop().transform(transform2).into(viewHolder.im4);
            Picasso.get().load(urls.get(4)).resize(200, 200).
                    centerCrop().transform(transform2).into(viewHolder.im5);
        }
        else if (post.getPhotonum() == 4) {
            Picasso.get().load(urls.get(0)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im1);
            Picasso.get().load(urls.get(1)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im2);
            Picasso.get().load(urls.get(2)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im3);
            Picasso.get().load(urls.get(3)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im4);
        }
        else if (post.getPhotonum() == 3) {
            Picasso.get().load(urls.get(0)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im1);
            Picasso.get().load(urls.get(1)).resize(540, 400).
                    centerCrop().transform(transform).into(viewHolder.im2);
            Picasso.get().load(urls.get(2)).resize(1100, 400).
                    centerCrop().transform(transform).into(viewHolder.im3);
        }
        else if (post.getPhotonum() == 2) {
            Picasso.get().load(urls.get(0)).resize(540, 640).
                    centerCrop().transform(transform).into(viewHolder.im1);
            Picasso.get().load(urls.get(1)).resize(540, 640).
                    centerCrop().transform(transform).into(viewHolder.im2);
        }
        else if (post.getPhotonum() == 1) {
            //Picasso.get().load(urls.get(0)).resize(1400,900).into(viewHolder.im1);
            Picasso.get().load(urls.get(0)).resize(1400, 900).
                    centerCrop().transform(transform).into(viewHolder.im1);
        }


        /*
        viewHolder.im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(0);
                Intent intent = new Intent(getContext(), PicassoSampleActivity.class);
                intent.putExtra("image", im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, view, "image1");
                context.startActivity(intent, options.toBundle());
            }
        });
        viewHolder.im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(1);
                Intent intent = new Intent(getContext(), PicassoSampleActivity.class);
                intent.putExtra("image", im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, view, "image1");
                context.startActivity(intent, options.toBundle());
            }
        });
        viewHolder.im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(2);
                Intent intent = new Intent(getContext(), PicassoSampleActivity.class);
                intent.putExtra("image", im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, view, "image1");
                context.startActivity(intent, options.toBundle());
            }
        });
        viewHolder.im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(3);
                Intent intent = new Intent(getContext(), PicassoSampleActivity.class);
                intent.putExtra("image", im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, view, "image1");
                context.startActivity(intent, options.toBundle());
            }
        });
        viewHolder.im5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String im = urls.get(4);
                Intent intent = new Intent(getContext(), PicassoSampleActivity.class);
                intent.putExtra("image", im);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, view, "image1");
                context.startActivity(intent, options.toBundle());
            }
        });
         */
        //Return the completed view to render on screen
        return convertView;
    }

    //★사진의 모서리를 둥굴게 하기위한 클래스
    class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;

        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);//매끄럽게 묘사 가능
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);//모소리를 정하는 클래스
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded(radius=" + Integer.toString(radius) + ", margin=" + Integer.toString(margin) + ")";
        }
    }
}
