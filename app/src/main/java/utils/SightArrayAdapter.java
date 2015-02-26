package utils;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kwmuch.kyle.sitemap.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import items.Sight;

/**
 * Created by Kyle on 1/24/2015.
 */
public class SightArrayAdapter extends ArrayAdapter<Sight> {
    private int sightLayout;

    public SightArrayAdapter(Context context, int sightLayout, ArrayList<Sight> sights) {
        super(context, 0, sights);
        this.sightLayout = sightLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SightHolder sightHolder;
        sightHolder = new SightHolder();

        sightHolder.sight = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(sightLayout, parent, false);
            sightHolder.sightTitle = (TextView) convertView.findViewById(R.id.sight_title);
            switch (sightLayout) {
                case R.layout.main_sight_list_item:
                    sightHolder.numPics = (TextView) convertView.findViewById(R.id.num_photos);
                    sightHolder.viewCollectionBtn = (ImageButton) convertView.findViewById(R.id.view_collections);
                    sightHolder.date = (TextView) convertView.findViewById(R.id.date_updated);
                    break;
                case R.layout.manage_sight_list_item:
                    sightHolder.editBtn = (ImageButton) convertView.findViewById(R.id.edit_sight);
                    sightHolder.editBtn.setTag(sightHolder.sight);
                    sightHolder.deleteBtn = (ImageButton) convertView.findViewById(R.id.delete_sight);
                    sightHolder.deleteBtn.setTag(new Integer(position));
//                    sightHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Integer tag = (Integer) v.getTag();
//                            deleteSight(tag);
//                            notifyDataSetChanged();
//                        }
//                    });
                    break;
            }
            convertView.setTag(sightHolder);
        } else {
            sightHolder = (SightHolder) convertView.getTag();
        }

        sightHolder.sightTitle.setText(sightHolder.sight.getmSiteName());

        switch (sightLayout) {
            case R.layout.main_sight_list_item:
                sightHolder.numPics.setText(sightHolder.sight.getmNumPics() + " Photos");
                sightHolder.viewCollectionBtn.setImageResource(R.drawable.ic_action_picture);
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                sightHolder.date.setText(sdf.format(sightHolder.sight.getmLastUpdated()));
                break;
            case R.layout.manage_sight_list_item:
                sightHolder.editBtn.setImageResource(R.drawable.ic_action_edit);
                sightHolder.deleteBtn.setImageResource(R.drawable.ic_action_remove);
                break;
        }

        return convertView;
    }

//    public void deleteSight(int position) {
//        Sight rmSight = getItem(position);
//        remove(rmSight);
//    }

    private static class SightHolder {
        Sight sight;
        TextView sightTitle;
        TextView numPics;
        TextView date;
        ImageButton viewCollectionBtn;
        ImageButton editBtn;
        ImageButton deleteBtn;
    }
}
