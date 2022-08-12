package com.cometchatworkspace.components.shared.sdkDerivedComponents.cometchatCalls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchatworkspace.R;

import java.util.ArrayList;
import java.util.List;

import com.cometchatworkspace.components.shared.primaryComponents.theme.Palette;
import com.cometchatworkspace.databinding.CometchatCallListRowBinding;
import com.cometchatworkspace.resources.utils.FontUtils;

/**
 * Purpose - CallListAdapter is a subclass of RecyclerView Adapter which is used to display
 * the list of calls. It helps to organize the list data in recyclerView.
 *
 * Created on - 23rd March 2020
 *
 * Modified on  - 02nd April 2020
 *
 */

public class CometChatCallsAdapter extends RecyclerView.Adapter<CometChatCallListItem> {

    private final Context context;

    private List<BaseMessage> callList = new ArrayList<>();
    private final Palette palette;
    private final FontUtils fontUtils;

    private final String loggedInUser = CometChat.getLoggedInUser().getUid();
    /**
     * It is constructor which takes callList as parameter and bind it with callList in adapter.
     *
     * @param context is a object of Context.
     * @param callList is list of calls used in this adapter.
     */
    public CometChatCallsAdapter(Context context, List<BaseMessage> callList) {
        this.callList = callList;
        this.context = context;
        palette=Palette.getInstance(context);
        fontUtils=FontUtils.getInstance(context);
    }

    /**
     * It is a constructor which is used to initialize wherever we needed.
     *
     * @param context
     */
    public CometChatCallsAdapter(Context context) {
        this.context = context;
        palette=Palette.getInstance(context);
        fontUtils=FontUtils.getInstance(context);

    }

    @NonNull
    @Override
    public CometChatCallListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        CometchatCallListRowBinding callListRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.cometchat_call_list_row, parent, false);

        return new CometChatCallListItem(callListRowBinding,context);
    }

    /**
     *  This method is used to bind the ConversationViewHolder contents with conversation at given
     *  position. It set avatar, name, lastMessage, unreadMessageCount and messageTime of conversation
     *  in a respective ConversationViewHolder content. It checks whether conversation type is user
     *  or group and set name and avatar as accordingly. It also checks whether last message is text, media
     *  or file and modify txtUserMessage view accordingly.
     *
     * @param cometChatCallListItem is a object of CometCallListItem.
     * @param position is a position of item in recyclerView.
     *
     * @see Conversation
     * @see CometChatCallListItem
     */
    @Override
    public void onBindViewHolder(@NonNull CometChatCallListItem cometChatCallListItem, int position) {
        BaseMessage baseMessage = callList.get(position);
        Call call = (Call)baseMessage;
        String avatar;
        String uid;
        String type;
        boolean isIncoming,isVideo,isMissed=false;
        String name;
        String callMessageText;
        String callType;
        String callCategory;
        if(call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            if (((User)call.getCallInitiator()).getUid().equals(loggedInUser)) {
                String callName = ((User)call.getCallReceiver()).getName();
                String avatarUrl = ((User)call.getCallReceiver()).getAvatar();
                cometChatCallListItem.setTitle(callName);
                if(avatarUrl!=null)
                    cometChatCallListItem.setAvatar(avatarUrl,"");
                else
                    cometChatCallListItem.setAvatar("",callName);

                if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED) || call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)) {
                    callMessageText = context.getResources().getString(R.string.missed_call);
                    isMissed = true;
                } else if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)) {
                    callMessageText = context.getResources().getString(R.string.rejected_call);
                } else
                    callMessageText = context.getResources().getString(R.string.outgoing);
                uid = ((User) call.getCallReceiver()).getUid();
                isIncoming = false;
            } else {
                String callName = ((User)call.getCallInitiator()).getName();
                cometChatCallListItem.setTitle(callName);
                cometChatCallListItem.getAvatar().setAvatar((User)call.getCallInitiator());
                if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED) || call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)) {
                    callMessageText = context.getResources().getString(R.string.missed_call);
                    isMissed = true;
                } else if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)) {
                    callMessageText = context.getResources().getString(R.string.rejected_call);
                } else
                    callMessageText = context.getResources().getString(R.string.incoming);
                uid = call.getSender().getUid();
                isIncoming = true;
            }
            type = CometChatConstants.RECEIVER_TYPE_USER;
        } else {
            cometChatCallListItem.setTitle(((Group)call.getCallReceiver()).getName());
            cometChatCallListItem.getAvatar().setAvatar(((Group)call.getCallReceiver()));
            if (((User)call.getCallInitiator()).getUid().equals(loggedInUser))
            {
                if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)) {
                    callMessageText = context.getResources().getString(R.string.missed_call);
                    isMissed = true;
                } else if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)) {
                    callMessageText = context.getResources().getString(R.string.rejected_call);
                } else
                    callMessageText = context.getResources().getString(R.string.incoming);
                isIncoming = false;
            }
            else
            {
                if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)) {
                    callMessageText = context.getResources().getString(R.string.missed_call);
                    isMissed = true;
                } else if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)) {
                    callMessageText = context.getResources().getString(R.string.rejected_call);
                } else
                    callMessageText = context.getResources().getString(R.string.incoming);
                isIncoming = true;
            }
            uid = ((Group) call.getCallReceiver()).getGuid();
            type = CometChatConstants.RECEIVER_TYPE_GROUP;
        }
        if(call.getType().equals(CometChatConstants.CALL_TYPE_VIDEO))
        {
            callMessageText = callMessageText+" "+context.getResources().getString(R.string.video_call);
            isVideo = true;
        }
        else
        {
            callMessageText = callMessageText+" "+context.getResources().getString(R.string.audio_call);
            isVideo = false;
        }
        if (isVideo)
        {
            if(isIncoming) {
                cometChatCallListItem.setStatus(R.drawable.ic_incoming_video_call,0);
            } else {
                cometChatCallListItem.setStatus(R.drawable.ic_outgoing_video_call,0);
            }
        }
        else
        {
            if (isIncoming && isMissed) {
                cometChatCallListItem.setStatus(R.drawable.ic_call_missed_incoming_24dp,0);
            } else if(isIncoming && !isMissed) {
                cometChatCallListItem.setStatus(R.drawable.ic_incoming_call,0);
            } else if (!isIncoming && isMissed) {
                cometChatCallListItem.setStatus(R.drawable.ic_call_missed_outgoing_24dp,0);
            } else {
                cometChatCallListItem.setStatus(R.drawable.ic_outgoing_call,0);
            }
        }
        cometChatCallListItem.setTime(call.getInitiatedAt());
        cometChatCallListItem.setSubTitle(callMessageText);
        cometChatCallListItem.getView().setTag(R.string.call, call);
        if (call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
                cometChatCallListItem.showButton(true);
        }

        cometChatCallListItem.showButtonColor(ColorStateList.valueOf(
                palette.getPrimary()));

    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    /**
     * This method is used to update the callList with new calls and avoid
     * duplicates call entries.
     *
     * @param calls is a list of calls which will be updated in adapter.
     */
    public void updateList(List<BaseMessage> calls) {

        callList.addAll(filterList(calls));
        notifyDataSetChanged();
    }


    private List<BaseMessage> filterList(List<BaseMessage> messageList)
    {
        ArrayList<BaseMessage> filteredList = new ArrayList<>();
        for (BaseMessage baseMessage : messageList)
        {
            if (((Call)baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)
                    || (((Call) baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_ENDED))
                    || (((Call) baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED))
                    || (((Call) baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED))) {
                filteredList.add(baseMessage);
            }
        }
        return filteredList;
    }

    /**
     * This method is used to remove the call from callList
     *
     * @param call is a object of Call.
     *
     * @see Call
     *
     */
    public void remove(Call call) {
        int position = callList.indexOf(call);
        callList.remove(call);
        notifyItemRemoved(position);
    }


    /**
     * This method is used to update call in callList.
     *
     * @param call is an object of Call. It is used to update the previous call
     *                     in list
     * @see Call
     */
    public void update(Call call) {

        if (callList.contains(call)) {
            Call oldCall = (Call)callList.get(callList.indexOf(call));
            callList.remove(oldCall);
            callList.add(0, call);
        } else {
            callList.add(0, call);
        }
        notifyDataSetChanged();

    }

    /**
     * This method is used to add call in list.
     *
     * @param call is an object of Call. It will be added to callList.
     *
     * @see Call
     */
    public void add(Call call) {
        if (callList != null)
            callList.add(call);
    }

    /**
     * This method is used to reset the adapter by clearing filterConversationList.
     */
    public void resetAdapterList() {
        callList.clear();
        notifyDataSetChanged();
    }
}
