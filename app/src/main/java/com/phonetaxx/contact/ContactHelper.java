package com.phonetaxx.contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.phonetaxx.R;
import com.phonetaxx.model.ContactModel;
import com.phonetaxx.utils.MediaConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactHelper {
    private static final String TAG = ContactHelper.class.getSimpleName();

    public static ModelContact getContactDetails(final Context context, String contactId) {

        ModelContact contact = new ModelContact();
        contact.setContactId(contactId);

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID,
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.Data.RAW_CONTACT_ID,
                        ContactsContract.Data.LOOKUP_KEY,
                        ContactsContract.Data.DATA1,
                        ContactsContract.Data.DATA2,
                        ContactsContract.Data.DATA3,
                        ContactsContract.Data.DATA4,
                        ContactsContract.Data.DATA5,
                        ContactsContract.Data.DATA6,
                        ContactsContract.Data.DATA7,
                        ContactsContract.Data.DATA8,
                        ContactsContract.Data.DATA10},

                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + "(" + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' OR "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' OR "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "' OR "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' OR "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "')",
                new String[]{contactId}, null);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                contact.setDisplayName(displayName);
                String rowContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                contact.setRawContactId(rowContactId);
                String lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
                contact.setLookupKey(lookUpKey);

                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    setPhoneList(cursor, contact);
                } else if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    setEmailList(cursor, contact);
                } else if (mimeType.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                    setStructuredName(cursor, contact);
                } else if (mimeType.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                    setOrganization(cursor, contact);
                } else if (mimeType.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
                    setAddress(cursor, contact);
                }
            }

            cursor.close();
        }
        return contact;
    }

    private static void setPhoneList(Cursor cursor, ModelContact contact) {
        // Phone
        PhoneOrEmail phone = new PhoneOrEmail();
        String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        String phoneType = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

        String phoneLabel = "";
        if (phoneType != null) {
            if (phoneType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM))) {
                phoneLabel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            } else {
                phoneLabel = getPhoneType(Integer.valueOf(phoneType));
            }
        }

        phone.setLabel(phoneLabel);
        phone.setValue(phoneNo);
        // if contact no is not match with any contact no in list
        if (!contact.getPhoneList().contains(phone)) {
            contact.getPhoneList().add(phone);
        }
    }

    private static void setEmailList(Cursor cursor, ModelContact contact) {
        // Email
        PhoneOrEmail email = new PhoneOrEmail();
        String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        String emailType = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

        String emailLabel = "";
        if (emailType != null) {
            if (emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM))) {
                emailLabel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
            } else {
                emailLabel = getEmailType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))));
            }
        }
        email.setLabel(emailLabel);
        email.setValue(emailAddress);
        contact.getEmailList().add(email);
    }

    private static void setStructuredName(Cursor cursor, ModelContact contact) {
        contact.setFirstName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
        contact.setLastName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
    }

    private static void setAddress(Cursor cursor, ModelContact contact) {
        // Address
        Address address = new Address();
        address.setStreet(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
        address.setCity(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
        address.setRegion(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)));
        address.setCountry(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)));

        String addressType = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
        if (addressType != null && addressType.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM))) {
            address.setLabel(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)));
        } else {
            if (addressType != null) {
                address.setLabel(getAddressType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)))));
            }
        }
        contact.getAddressList().add(address);
    }

    private static void setOrganization(Cursor cursor, ModelContact contact) {
        contact.setCompany(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)));
        contact.setJobTitle(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
        contact.setDepartment(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT)));
    }

    private static String getPhoneType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return "Assistant";
            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return "Callback";
            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return "Car";
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return "Company Main";
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return "Fax Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "Fax Work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return "ISDN";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return "Main";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return "MMS";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return "Other Fax";
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return "Pager";
            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return "Radio";
            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return "Telex";
            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return "TTY TDD";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return "Work Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return "Work Pager";
        }
        return "";
    }

    private static String getEmailType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                return "Mobile";
            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                return "Other";
            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                return "Work";
        }
        return "";
    }

    private static String getAddressType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                return "Other";
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                return "Work";
        }
        return "";
    }

    public static byte[] getContactPhoto(Context context, String id) {
        if (!TextUtils.isEmpty(id)) {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id)), true);

            if (inputStream != null) {
                try {
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    return b;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new byte[0];
    }

    public static Uri getPhotoUri(Context context, long contactId) {
        ContentResolver contentResolver = context.getContentResolver();

        try {
            Cursor cursor = contentResolver
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + contactId
                                    + " AND "

                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    // Using ContactsContract.Contacts -> this take more time
    public static void getContactList(Context context) {
        long startTime = System.currentTimeMillis();

        int count = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        count++;
                        Log.e(TAG, "Name/Number : " + name + " - " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        Log.e(TAG, "getContactList: " + (System.currentTimeMillis() - startTime) + " -> Count: " + count);
    }

    // Using ContactsContract.Data -> this take less time
    public static List<ContactModel> getContactListNew(Context context) {


        List<ContactModel> contactList = new ArrayList<ContactModel>();
        List<String> phoneList = new ArrayList<String>();
        long startTime = System.currentTimeMillis();

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID,
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.Data.HAS_PHONE_NUMBER,
                        ContactsContract.Data.DATA1},

                ContactsContract.Data.HAS_PHONE_NUMBER + ">0" + " AND "
                        + "(" + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')",
                null, null);

        MediaConst.getInstance().CreateFileStructure();
        Bitmap bitmap = null;
        int count = 0;
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

                ContactModel contactModel = new ContactModel();
                contactModel.setContactName(displayName);

                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!phoneList.contains(phoneNo.trim())) {
                        phoneList.add(phoneNo.trim());
                        try {
                            bitmap = retrieveContactPhoto(context, phoneNo);
                            String filename = id + ".png";
                            File sd = new File(MediaConst.getInstance().IMAGE_DIRECTORY_PATH);
                            File dest = new File(sd, filename);
                            try {
                                FileOutputStream out = new FileOutputStream(dest);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            contactModel.setContactUri(dest.getPath());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        contactModel.setContactNumber(phoneNo.trim());
                        count++;
                        contactList.add(contactModel);
                    }

                }
            }

            Log.e(TAG, "getContactListNew: " + (System.currentTimeMillis() - startTime) + " -> Count: " + count);
            cursor.close();

        }
        return contactList;
    }

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_contact_placeholder);

        try {
            if (contactId != null) {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                assert inputStream != null;
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public static class ModelContact {

        private String contactId = "";
        private String rawContactId = "";
        private String firstName = "";
        private String lastName = "";
        private String displayName = "";
        private String lookupKey = "";
        private List<PhoneOrEmail> phoneList = new ArrayList<>();
        private List<PhoneOrEmail> emailList = new ArrayList<>();
        private List<Address> addressList = new ArrayList<>();
        private String company = "";
        private String jobTitle = "";
        private String department = "";

        public ModelContact() {
        }

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }

        public String getRawContactId() {
            return rawContactId;
        }

        public void setRawContactId(String rawContactId) {
            this.rawContactId = rawContactId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getLookupKey() {
            return lookupKey;
        }

        public void setLookupKey(String lookupKey) {
            this.lookupKey = lookupKey;
        }

        public List<PhoneOrEmail> getPhoneList() {
            return phoneList;
        }

        public void setPhoneList(List<PhoneOrEmail> phoneList) {
            this.phoneList = phoneList;
        }

        public List<PhoneOrEmail> getEmailList() {
            return emailList;
        }

        public void setEmailList(List<PhoneOrEmail> emailList) {
            this.emailList = emailList;
        }

        public List<Address> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<Address> addressList) {
            this.addressList = addressList;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }

    public static class PhoneOrEmail {

        private String label;
        private String value;

        public PhoneOrEmail(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public PhoneOrEmail() {
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class Address {

        private String street = "";
        private String city = "";
        private String region = "";
        private String country = "";
        private String label = "";

        public Address() {
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
