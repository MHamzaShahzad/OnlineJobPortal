package com.example.onlinejobportal.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static android.view.Gravity.END;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.Holder> {

    private Context context;
    private List<ChatModel> chatModelList;
    private FirebaseUser firebaseUser;

    private LinearLayout.LayoutParams params;

    public AdapterChat(Context context, List<ChatModel> chatModelList) {
        this.context = context;
        this.chatModelList = chatModelList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_card, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ChatModel chatModel = chatModelList.get(holder.getAdapterPosition());

        if (chatModel.getSenderId().equals(firebaseUser.getUid())) {
            holder.cardChat.setBackgroundColor(context.getResources().getColor(R.color.myMessage));
            params.gravity = END;
        } else {
            holder.cardChat.setBackgroundColor(context.getResources().getColor(R.color.otherMessage));
            params.gravity = Gravity.START;
        }

        //holder.cardChat.setLayoutParams(params);


        holder.textMessage.setText(chatModel.getMessage());
        holder.textDateTimeSent.setText(chatModel.getDateTimeSent());
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView cardChat;
        private TextView textMessage, textDateTimeSent;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardChat = itemView.findViewById(R.id.cardChat);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTimeSent = itemView.findViewById(R.id.textDateTimeSent);
        }
    }
}
