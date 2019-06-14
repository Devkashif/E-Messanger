package com.example.mdkashif.new_fire;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdkashif.new_fire.Notification.Client;
import com.example.mdkashif.new_fire.Notification.Data;
import com.example.mdkashif.new_fire.Notification.MyResponse;
import com.example.mdkashif.new_fire.Notification.Sender;
import com.example.mdkashif.new_fire.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String MsgRcvId;
    String MsgRcvName;
    ImageView callingimg;
    Toolbar chatToolbar;
    DatabaseReference RootRef;
    DatabaseReference onlineRef;
    TextView chatusername;
    TextView chatlastseen;
    CircleImageView chatImgView;
    FirebaseAuth cAuth;
    FirebaseUser currentUser;
    FirebaseUser fuser;
    ImageButton send_msg_button;
    ImageButton send_img;
    EditText text_msg;
    String msgSenderId;
    RecyclerView chatmsglist;
    String Msg_Sender_Ref;
    String Msg_Rcv_Ref;
    StorageReference senderImgStorage;
    SwipeRefreshLayout refreshChat;
    private final static int LOAD_TOTAL_MSG =10;
    private int chatCurrentPage = 1;
    private final List<Message> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;
    private final static int GALARAY_PIC = 1;

    private int itemPoss = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    ProgressDialog proidialog;

    APIServices apiServices;
    boolean notify = false;
    String userid;
    Intent intent;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        proidialog = new ProgressDialog(this);
        RootRef = FirebaseDatabase.getInstance().getReference();
        cAuth = FirebaseAuth.getInstance();
        msgSenderId = cAuth.getCurrentUser().getUid();
        refreshChat = findViewById(R.id.swipechat);
        messageAdapter = new MessageAdapter(messagesList);
        chatmsglist = findViewById(R.id.MsgRetriewlist);
        linearLayoutManager = new LinearLayoutManager(this);
        chatmsglist.setHasFixedSize(true);
        chatmsglist.setLayoutManager(linearLayoutManager);
        chatmsglist.setAdapter(messageAdapter);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        MsgRcvId = cAuth.getCurrentUser().getUid();
        MsgRcvName =getIntent().getExtras().get("Name").toString();

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        MsgRcvId = getIntent().getExtras().get("select_user_id").toString();

        senderImgStorage = FirebaseStorage.getInstance().getReference().child("Messages_Image");

        apiServices = Client.getClient("http://fcm.googleapis.com/").create(APIServices.class);
       /* sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(msgSenderId)
              //  .applicationKey("")
              //  .applicationSecret("")
                //.environmentHost("")
                .build();*/

  /*      sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new sinchClientListner(){

        });

    sinchClient.start();*/

        FetchMessage();




        if(currentUser != null){
            String Online_User = cAuth.getCurrentUser().getUid();
            onlineRef = FirebaseDatabase.getInstance().getReference().child("User").child(Online_User);

        }



        send_msg_button = findViewById(R.id.send_msg);
        send_img = findViewById(R.id.select_img);
        text_msg = findViewById(R.id.input_msg);

        chatToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //here inflate the chat_profile_layout into Chat Activity on Toolbar

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_layout = layoutInflater.inflate(R.layout.chat_profile_layout, null);
        actionBar.setCustomView(action_bar_layout);

        chatusername = findViewById(R.id.fndChatNam);
        chatlastseen = findViewById(R.id.fndChatSeen);
        chatImgView = findViewById(R.id.fndChatimg);

        chatusername.setText(MsgRcvName);

        callingimg = findViewById(R.id.call);
        callingimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence option[] = new CharSequence[]{
                        "Voice Call",
                        "Video Call"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Chose a Call");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(ChatActivity.this, "Waiting For Connection", Toast.LENGTH_SHORT).show();

                    }
                });builder.show();
            }
        });

        //here retrieve the last seen and user thumb image into chat profile layout

        RootRef.child("User").child(MsgRcvId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String online = dataSnapshot.child("Online").getValue().toString();
                final String chatuserImg = dataSnapshot.child("Thumb_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(chatuserImg).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefalt)
                        .into(chatImgView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(ChatActivity.this).load(chatuserImg).placeholder(R.drawable.userdefalt).into(chatImgView);
                            }
                        });
                if (online.equals("true")){

                    chatlastseen.setText("Online");

                }else {

                    LastSeen gettime = new LastSeen();
                    long last_seen = Long.parseLong(online);
                    String lastseenDisplayTime = gettime.getTimeAgo(last_seen);
                    chatlastseen.setText(lastseenDisplayTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send_msg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                SendMessage();

            }
        });

        refreshChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                itemPoss = 0;
                chatCurrentPage++;
                FetchMoreMessage();
            }
        });

        send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galary_intent = new Intent();
                galary_intent.setAction(Intent.ACTION_GET_CONTENT);
                galary_intent.setType("image/*");
                startActivityForResult(galary_intent,GALARAY_PIC);
            }
        });
    }



    private void FetchMoreMessage() {

        DatabaseReference msgref = RootRef.child("Messages").child(msgSenderId).child(MsgRcvId);
        Query msgQuery = msgref.orderByKey().endAt(mLastKey).limitToLast(10);

        msgQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();
                if (!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPoss++,message);
                }else {

                    mPrevKey = messageKey;
                }

                if (itemPoss == 1){


                    mLastKey = messageKey;

                }
                messageAdapter.notifyDataSetChanged();
                chatmsglist.smoothScrollToPosition(chatmsglist.getAdapter().getItemCount());
                refreshChat.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //Retriew msg
    private void FetchMessage() {

        DatabaseReference msgREf = RootRef.child("Messages").child(msgSenderId).child(MsgRcvId);
        Query msgQuery = msgREf.limitToLast(chatCurrentPage * LOAD_TOTAL_MSG);

        msgQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                itemPoss++;
                if (itemPoss == 1){
                    String messageKey = dataSnapshot.getKey();
                      mLastKey = messageKey;
                      mPrevKey = messageKey;
                }
                Message messages = dataSnapshot.getValue(Message.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                chatmsglist.smoothScrollToPosition(chatmsglist.getAdapter().getItemCount());
                refreshChat.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendMessage() {

        String MessageText = text_msg.getText().toString();

        if (TextUtils.isEmpty(MessageText)){

        }
        else {

           String Msg_Sender_Ref = "Messages/" + msgSenderId + "/" + MsgRcvId;
           String Msg_Rcv_Ref = "Messages/" + MsgRcvId + "/" + msgSenderId;

            DatabaseReference user_msg_key = RootRef.child("Messages").child(msgSenderId).child(MsgRcvId).push();
            String push_id = user_msg_key.getKey();

            Map MsgTextBody = new HashMap();
            MsgTextBody.put("Message", MessageText);
            MsgTextBody.put("Seen", false);
            MsgTextBody.put("Type", "text");
            MsgTextBody.put("Time", ServerValue.TIMESTAMP);
            MsgTextBody.put("from", msgSenderId);


            Map MsgBodyDetail = new HashMap();
            MsgBodyDetail.put(Msg_Sender_Ref + "/" + push_id, MsgTextBody);
            MsgBodyDetail.put(Msg_Rcv_Ref + "/" + push_id, MsgTextBody);

            RootRef.updateChildren(MsgBodyDetail, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if (databaseError != null){
                        Log.e("Chat_log_Error", databaseError.getMessage().toString());
                    }

                    text_msg.setText("");

                }
            });


            final String msg = MessageText;
            user_msg_key = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
            user_msg_key.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Message message = dataSnapshot.getValue(Message.class);
                    if (notify){

                        sendNotificaton(MsgRcvId, message.getFrom(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void sendNotificaton(final String msgRcvId, final String from, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(MsgRcvId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.cahtinglogo,from+": "+msg,"new Message",userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiServices.sendNotification(sender).enqueue(new retrofit2.Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code()==200){
                                if (response.body().success != 1){
                                    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

              if (currentUser != null){

            onlineRef.child("Online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null){
            onlineRef.child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }


   /* private class sinchClientListner implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            //open dialog for new incoming call
        }
    }*/
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == GALARAY_PIC && resultCode==RESULT_OK && data!=null){

           Uri image_uri = data.getData();

           Msg_Sender_Ref =  "Messages/" + msgSenderId + "/" + MsgRcvId;
           Msg_Rcv_Ref =  "Messages/" + MsgRcvId + "/" + msgSenderId;

           DatabaseReference user_msg_key = RootRef.child("Messages").child(msgSenderId).child(MsgRcvId).push();
           final String msg_push_id = user_msg_key.getKey();

           final StorageReference filepath = senderImgStorage.child(msg_push_id + ".jpg");
           proidialog.show();
           proidialog.setTitle("Sending Image...");
           proidialog.setMessage("Please Wait While your Sending Image");
           filepath.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                   if (task.isSuccessful()){


                       filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {

                               final String dounloadImgUrl = uri.toString();

                               Map imagedetailmsg = new HashMap();
                               imagedetailmsg.put("Message", dounloadImgUrl);
                               imagedetailmsg.put("seen",false);
                               imagedetailmsg.put("Type","image");
                               imagedetailmsg.put("Time",ServerValue.TIMESTAMP);
                               imagedetailmsg.put("from",msgSenderId);

                               Map BodyHasmap = new HashMap();
                               BodyHasmap.put(Msg_Sender_Ref + "/" + msg_push_id , imagedetailmsg);
                               BodyHasmap.put(Msg_Rcv_Ref + "/" + msg_push_id , imagedetailmsg);

                               RootRef.updateChildren(BodyHasmap, new DatabaseReference.CompletionListener() {
                                   @Override
                                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                       if (databaseError != null){
                                           Log.d("Sent_Image_Log_Detail", databaseError.getMessage().toString());
                                       }
                                       text_msg.setText("");
                                       proidialog.dismiss();
                                   }
                               });
                               Toast.makeText(ChatActivity.this, "Photo Send Successfully", Toast.LENGTH_SHORT).show();
                               proidialog.dismiss();
                           }
                       });
                   }
                   else {
                       proidialog.cancel();
                       Toast.makeText(ChatActivity.this, "Image Not send... Try again ", Toast.LENGTH_SHORT).show();
                   }

               }
           });
       }
   }
}
