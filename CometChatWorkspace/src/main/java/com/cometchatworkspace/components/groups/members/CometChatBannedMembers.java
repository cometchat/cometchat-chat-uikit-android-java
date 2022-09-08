package com.cometchatworkspace.components.groups.members;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchatworkspace.R;
import com.cometchatworkspace.components.shared.primaryComponents.CometChatListBase;
import com.cometchatworkspace.components.shared.primaryComponents.configurations.CometChatConfigurations;
import com.cometchatworkspace.components.shared.primaryComponents.theme.Palette;
import com.cometchatworkspace.components.shared.primaryComponents.theme.Typography;
import com.cometchatworkspace.components.shared.sdkDerivedComponents.cometchatBannedMemberList.CometChatBannedMemberList;
import com.cometchatworkspace.resources.utils.CometChatError;
import com.cometchatworkspace.resources.utils.FontUtils;
import com.cometchatworkspace.resources.utils.Utils;
import com.cometchatworkspace.resources.utils.item_clickListener.OnItemClickListener;

import java.util.HashMap;
import java.util.List;

public class CometChatBannedMembers extends CometChatListBase {

    private CometChatBannedMemberList cometchatBannedMemberList;
    private Context context;
    private FontUtils fontUtils;
    private View view;
    private Palette palette;
    private Typography typography;
    private Group group;
    private static final HashMap<String, Events> events = new HashMap<>();
    private boolean allowBanUnbanMembers;


    public CometChatBannedMembers(Context context) {
        super(context);
        initViewComponent(context, null, -1);

    }

    public CometChatBannedMembers(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            initViewComponent(context, attrs, -1);
    }

    public CometChatBannedMembers(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            initViewComponent(context, attrs, defStyleAttr);
    }

    private void initViewComponent(Context context, AttributeSet attributeSet, int defStyleAttr) {

        fontUtils = FontUtils.getInstance(context);
        this.context = context;
        palette = Palette.getInstance(context);
        typography = Typography.getInstance();

        view = View.inflate(context, R.layout.cometchat_banned_members, null);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.CometChatBannedMembers,
                0, 0);
        cometchatBannedMemberList = view.findViewById(R.id.cometchatMemberList);

        String title = a.getString(R.styleable.CometChatGroupMembers_title) == null ? getResources().getString(R.string.members) : a.getString(R.styleable.CometChatGroupMembers_title);

        int titleColor = a.getColor(R.styleable.
                CometChatBannedMembers_titleColor, palette.getAccent());
        boolean hideSearchBox = a.getBoolean(R.styleable.
                CometChatBannedMembers_hideSearch, false);
        float searchBoxRadius = a.getDimension(R.styleable.
                CometChatBannedMembers_searchCornerRadius, 0f);
        int searchBoxColor = a.getColor(R.styleable.
                CometChatBannedMembers_searchBackgroundColor, palette.getAccent50());
        int searchTextColor = a.getColor(R.styleable.
                CometChatBannedMembers_searchTextColor, palette.getAccent600());
        int searchBorderWidth = (int) a.getDimension(R.styleable.CometChatBannedMembers_searchBorderWidth, 0f);
        int searchBorderColor = a.getColor(R.styleable.CometChatBannedMembers_searchBorderColor, 0);
        int backgroundColor = a.getColor(R.styleable.CometChatBannedMembers_backgroundColor,palette.getBackground());
        boolean showBackButton = a.getBoolean(R.styleable.CometChatBannedMembers_showBackButton, true);

        Drawable backButtonIcon = a.getDrawable(R.styleable.CometChatBannedMembers_backButtonIcon) != null ? a.getDrawable(R.styleable.CometChatBannedMembers_backButtonIcon) : getResources().getDrawable(R.drawable.ic_arrow_back);
        int listBackgroundColor = a.getColor(R.styleable.
                CometChatBannedMembers_listBackgroundColor, getResources().getColor(android.R.color.transparent));
        String searchPlaceholder = a.getString(R.styleable.CometChatBannedMembers_searchPlaceholder);
        allowBanUnbanMembers = a.getBoolean(R.styleable.CometChatBannedMembers_allowBanUnbanMembers, true);
        int backIconTint = a.getColor(R.styleable.CometChatBannedMembers_backIconTint, palette.getPrimary());
        //End of Handling Attributes

        //Below method will set color of StatusBar.
        setStatusColor(palette.getBackground());

        super.showBackButton(showBackButton);
        super.backIcon(backButtonIcon);
        super.backIconTint(backIconTint);
        super.addSearchViewPlaceHolder(searchPlaceholder);
        super.setTitle(title);
        super.setSearchTextAppearance(typography.getText1());
        super.setTitleAppearance(typography.getHeading());
        emptyStateTextAppearance(typography.getHeading());
        emptyStateTextColor(palette.getAccent400());
        super.setTitleColor(titleColor);
        super.setSearchTextColor(searchTextColor);
        super.setSearchBorderColor(searchBorderColor);
        super.setSearchBorderWidth(searchBorderWidth);
        super.setSearchPlaceHolderColor(searchTextColor);
        setListBackgroundColor(listBackgroundColor);
        super.setSearchBackground(searchBoxColor);
        super.hideSearch(hideSearchBox);
        super.setSearchCornerRadius(searchBoxRadius);

        if (palette.getGradientBackground() != null)
            setBackground(palette.getGradientBackground());
        else
            setBackgroundColor(backgroundColor);

        super.addListView(view);
        CometChatError.init(getContext());
        super.addEventListener(new OnEventListener() {
            @Override
            public void onSearch(String state, String text) {
                if (state.equals(SearchState.Filter)) {
                    cometchatBannedMemberList.searchBannedMembers(text);
                } else if (state.equals(SearchState.TextChange)) {
                    if (text.length() == 0) {
//                    // if searchEdit is empty then fetch all groups.
                        cometchatBannedMemberList.clearList();
                    } else {
//                    // Search groups based on text in searchEdit field.
                        cometchatBannedMemberList.searchBannedMembers(text);
                    }
                }
            }

            @Override
            public void onBack() {
                ((Activity) context).onBackPressed();
            }
        });

        cometchatBannedMemberList.setItemClickListener(new OnItemClickListener<GroupMember>() {
            @Override
            public void OnItemClick(GroupMember groupMember, int position) {
                if (events != null) {
                    for (Events e : events.values()) {
                        e.OnItemClick(groupMember, position);
                    }
                }
            }
        });


    }

    public void setStatusColor(int color) {
        Utils.setStatusBarColor(context,color);

    }
    public void allowBanUnbanMembers(boolean allowBanUnbanMembers) {
        this.allowBanUnbanMembers = allowBanUnbanMembers;
        cometchatBannedMemberList.allowBanUnbanMembers(allowBanUnbanMembers);
    }

    public void setGroup(Group group) {
        this.group = group;
        cometchatBannedMemberList.setGroup(group);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    /**
     * This method is used to set the icon for back button.
     *
     * @param res this method is exposed for the user to change the backIcon as per there preference
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void setBackIcon(int res) {
        if (res != 0)
            super.backIcon(res);
    }


    /**
     * This method is used to change the text color of Empty State Text
     *
     * @param color
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void emptyStateTextColor(@ColorInt int color) {
        if (cometchatBannedMemberList != null && color != 0)
            cometchatBannedMemberList.emptyTextColor(color);
    }

    /**
     * This method is used to change the text Appearance of Empty State Text
     *
     * @param appearance
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void emptyStateTextAppearance(int appearance) {
        if (cometchatBannedMemberList != null && appearance != 0)
            cometchatBannedMemberList.emptyStateTextAppearance(appearance);
    }

    /**
     * This method is used to change the font of Empty State Text.
     *
     * @param font
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void emptyStateTextFont(String font) {
        if (cometchatBannedMemberList != null && font != null)
            cometchatBannedMemberList.emptyStateTextFont(font);

    }

    public static void addListener(String TAG, Events<Group> onEventListener) {
        events.put(TAG, onEventListener);
    }

    private void setStatusBarColor() {
        int backgroundColor = palette.getPrimary();

        if (backgroundColor != 0)
            ((Activity) context).getWindow().setStatusBarColor(backgroundColor);

    }

    public void setListBackgroundColor(@ColorInt int listBackgroundColor) {
        if (cometchatBannedMemberList != null) {
            if (listBackgroundColor != 0)
                cometchatBannedMemberList.setCardBackgroundColor(listBackgroundColor);
            else {
                cometchatBannedMemberList.setCardBackgroundColor(Color.TRANSPARENT);
                cometchatBannedMemberList.setCardElevation(0);
                cometchatBannedMemberList.setRadius(0);
            }
        }
    }

    /**
     * This method is used to set the configuration in CometChatGroups.
     *
     * @param configuration the configuration
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void setConfiguration(CometChatConfigurations configuration) {
        cometchatBannedMemberList.setConfigurations(configuration);
    }

    /**
     * This method is used to set the multiple configurations in CometChatGroups
     *
     * @param configurations the configurations
     * @author CometChat Team
     * Copyright &copy; 2021 CometChat Inc.
     */
    public void setConfigurations(List<CometChatConfigurations> configurations) {
        cometchatBannedMemberList.setConfigurations(configurations);
    }


    public abstract static class Events<T> {

        /**
         * It is triggered whenever any item from cometchatMemberList is clicked
         *
         * @param var      the var
         * @param position the position
         * @author CometChat Team
         * Copyright &copy; 2021 CometChat Inc.
         */
        public abstract void OnItemClick(T var, int position);

        /**
         * It is triggered whenever a long press action is performed
         * on the item from cometchatMemberList
         *
         * @param var      the var
         * @param position the position
         * @author CometChat Team
         * Copyright &copy; 2021 CometChat Inc.
         */
        public void OnItemLongClick(T var, int position) {

        }
    }

}