package com.ingwill.widget.listvisibility.visibility.scroll;


import com.ingwill.widget.listvisibility.visibility.items.ListItem;

/**
 * This interface is used by {@link com.ingwill.widget.listvisibility.visibility.calculator.SingleListViewItemActiveCalculator}.
 * Using this class to get {@link com.ingwill.widget.listvisibility.visibility.items.ListItem}
 *
 * @author Wayne
 */
public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
