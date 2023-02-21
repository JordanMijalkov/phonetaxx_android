package com.phonetaxx.custom.singleChoiceBottomSheet;

/**
 * Listener to handle click event of item
 *
 * @param <ItemType>
 */
public interface SingleChoiceDialogListener<ItemType> {
    void onSpinnerItemClick(int position, ItemType selectedItem);
}
