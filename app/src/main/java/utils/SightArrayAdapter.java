package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kwmuch.kyle.sitemap.R;

import java.util.ArrayList;

import items.Sight;

/**
 * Created by Kyle on 1/24/2015.
 */
public class SightArrayAdapter extends ArrayAdapter<Sight>
{
    private int sightLayout;
    private static class SightHolder
    {
        TextView sightTitle;
        TextView numPics;
        ImageButton viewCollectionBtn;
        ImageButton editBtn;
        ImageButton deleteBtn;
    }

    public SightArrayAdapter(Context context, ArrayList<Sight> sights, int sightLayout)
    {
        super(context, 0, sights);
        this.sightLayout = sightLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Sight sight = getItem(position);

        SightHolder sightHolder;

        if(convertView == null)
        {
            sightHolder = new SightHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(sightLayout, parent, false);
            sightHolder.sightTitle = (TextView) convertView.findViewById(R.id.sight_title);
            switch (sightLayout)
            {
                case R.layout.main_sight_list_item:
                    sightHolder.viewCollectionBtn = (ImageButton) convertView.findViewById(R.id.view_collections);
                    sightHolder.numPics = (TextView) convertView.findViewById(R.id.num_photos);
                    break;
                case R.layout.manage_sight_list_item:
                    sightHolder.editBtn = (ImageButton) convertView.findViewById(R.id.edit_sight);
                    sightHolder.deleteBtn = (ImageButton) convertView.findViewById(R.id.delete_sight);
                    break;
            }
            convertView.setTag(sightHolder);
        }
        else
        {
            sightHolder = (SightHolder) convertView.getTag();
        }

        sightHolder.sightTitle.setText(sight.getmSiteName());

        switch (sightLayout)
        {
            case R.layout.main_sight_list_item:
                sightHolder.numPics.setText(sight.getmNumPics() + " Photos");
                sightHolder.viewCollectionBtn.setImageResource(R.drawable.ic_action_picture);
                break;
            case R.layout.manage_sight_list_item:
                sightHolder.editBtn.setImageResource(R.drawable.ic_action_edit);
                sightHolder.deleteBtn.setImageResource(R.drawable.ic_action_remove);
                break;
        }

        return convertView;
    }
}
