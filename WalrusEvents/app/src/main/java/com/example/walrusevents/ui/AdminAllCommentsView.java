/**
 * View popes up when admin wants to view all comments to manage them
 */

package com.example.walrusevents.ui;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.walrusevents.R;

public class AdminAllCommentsView {
    private ImageView backButton;
    private ListView commentsList;

    public AdminAllCommentsView(Activity activity) {
        backButton = activity.findViewById(R.id.admin_comments_back_button);
        commentsList = activity.findViewById(R.id.admin_comments_list);
    }

    public ImageView getBackButton() {
        return backButton;
    }

    public ListView getCommentsList() {
        return commentsList;
    }
}
