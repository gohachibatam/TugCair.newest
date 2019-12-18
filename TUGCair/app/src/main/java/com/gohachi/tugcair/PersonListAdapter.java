package com.gohachi.tugcair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.provider.Settings.Global.AIRPLANE_MODE_ON;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gohachi.tugcair.signature.ViewDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static androidx.core.content.ContextCompat.startActivity;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {


    public List<Person> personList;
    public Context context;

    private FirebaseFirestore mDatabase;
    final String TAG = "PersonListAdapter";

    public PersonListAdapter(Context context,List<Person> personList){
        this.personList = personList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_persons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.mFullname.setText(personList.get(position).getFullname());
        holder.mNoKTP.setText(personList.get(position).getNo_card());
        holder.mAddress.setText(personList.get(position).getAddress());
        holder.mContactPhone.setText(personList.get(position).getNo_phone());

        // TODO : Firestore Initialization
        FirestoreInit();

        // TODO : Set icon on button
        holder.mBtnUpdatePerson.setImageResource(R.drawable.ic_edit);
        holder.mBtnDeletePerson.setImageResource(R.drawable.ic_rubbish_bin_delete_button);
        holder.mBtnShowLocation.setImageResource(R.drawable.ic_placeholder_filled_point);
//        holder.mCoordLocation.setText(personList.get(position).getLocationcoord());

        final String person_id = personList.get(position).personId;
        final String coord = personList.get(position).getLocationcoord();

        final String Namalengkap, NomorKTP, AlamatKTP, AlamatSekarang, ImageFilename, NomorKontak, Latcode, Longcode;

        // TODO : Get Fields by row
        Namalengkap = personList.get(position).getFullname();
        NomorKTP = personList.get(position).getNo_card();
        AlamatKTP = personList.get(position).getAddress();
        AlamatSekarang = personList.get(position).getAddress_now();
        NomorKontak = personList.get(position).getNo_phone();
        ImageFilename = personList.get(position).getFilename_signature();

        //TODO : Get Location Coordinate
        String[] separated = coord.split(",");
        Latcode = separated[0];
        Longcode = separated[1];


//        Toast.makeText(context, Latcode +" "+Longcode, Toast.LENGTH_SHORT).show();
//        Log.d("PersonListAdapter", "Lat Code : "+Latcode +", Long Code : "+Longcode);

        holder.mBtnUpdatePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Update Data Person

                Intent intent = new Intent(context, UpdatePersonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Person_Id", person_id);
                intent.putExtra("Nama_Lengkap", Namalengkap);
                intent.putExtra("Nomor_KTP", NomorKTP);
                intent.putExtra("Alamat_KTP", AlamatKTP);
                intent.putExtra("Alamat_Now", AlamatSekarang);
                intent.putExtra("Nomor_Kontak", NomorKontak);
                context.startActivity(intent);

                Log.d(TAG, "User id : "+person_id);
                Log.d(TAG, "Nama Lengkap : "+Namalengkap);
                Log.d(TAG, "Nomor KTP : "+NomorKTP);
                Log.d(TAG, "Alamat KTP : "+AlamatKTP);
                Log.d(TAG, "Alamat Sekarang : "+AlamatSekarang);
                Log.d(TAG, "Nomor Kontak : "+NomorKontak);
                Log.d(TAG, "Filename : "+ImageFilename);
            }
        });

        holder.mBtnDeletePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Delete Data Person
                        mDatabase.collection("persons").document(person_id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Data was successfully deleted!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Data was successfully deleted!");
                                        personList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, personList.size());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error deleting document : " +e , Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                    }
        });

        holder.mBtnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Show Location Person


                try {
                    Intent  intent = new Intent(context, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("Lat_Code", Latcode);
                    intent.putExtra("Long_Code", Longcode);
                    intent.putExtra("Address", AlamatSekarang);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Person ID : " +person_id, Toast.LENGTH_SHORT).show();
////                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(Latcode),  Double.parseDouble(Longcode));
////                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
////                context.startActivity(intent);
//
//                Intent intent = new Intent(context, ViewDetailActivity.class);
//                intent.putExtra("Person_Id", person_id);
//                intent.putExtra("Nama_Lengkap", Namalengkap);
//                intent.putExtra("Nomor_KTP", NomorKTP);
//                intent.putExtra("Alamat_KTP", AlamatKTP);
//                intent.putExtra("Alamat_Now", AlamatSekarang);
//                intent.putExtra("Nomor_Kontak", NomorKontak);
//                intent.putExtra("Filename_signature", ImageFilename);
//                context.startActivity(intent);
//
//                Log.d(TAG, "User id : "+person_id);
//                Log.d(TAG, "Nama Lengkap : "+Namalengkap);
//                Log.d(TAG, "Nomor KTP : "+NomorKTP);
//                Log.d(TAG, "Alamat KTP : "+AlamatKTP);
//                Log.d(TAG, "Alamat Sekarang : "+AlamatSekarang);
//                Log.d(TAG, "Nomor Kontak : "+NomorKontak);
//                Log.d(TAG, "Filename : "+ImageFilename);

                Intent intent = new Intent(context, ViewDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    intent.putExtra("Fullname", Namalengkap);
                    intent.putExtra("Nomor_KTP", NomorKTP);
                    intent.putExtra("Alamat_KTP", AlamatKTP);
                    intent.putExtra("Alamat_Now", AlamatSekarang);
                    intent.putExtra("Nomor_Kontak", NomorKontak);
                    intent.putExtra("Nomor_Kontak", NomorKontak);
                    intent.putExtra("Filename_signature", ImageFilename);
                    context.startActivity(intent);

                    Log.d(TAG, "User id : "+person_id);
                    Log.d(TAG, "Nama Lengkap : "+Namalengkap);
                    Log.d(TAG, "Nomor KTP : "+NomorKTP);
                    Log.d(TAG, "Alamat KTP : "+AlamatKTP);
                    Log.d(TAG, "Alamat Sekarang : "+AlamatSekarang);
                    Log.d(TAG, "Nomor Kontak : "+NomorKontak);
                    Log.d(TAG, "Filename : "+ImageFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                intent.putExtra("Person_Id", person_id);

            }
        });
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View mView;

        public TextView mFullname, mNoKTP, mAddress, mContactPhone, mCoordLocation;
        public ImageView mBtnUpdatePerson, mBtnDeletePerson, mBtnShowLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            mFullname = mView.findViewById(R.id.txViewFullname);
            mNoKTP = mView.findViewById(R.id.txViewNoKTP);
            mAddress = mView.findViewById(R.id.txViewAlamat);
            mContactPhone = mView.findViewById(R.id.txViewNoContact);
//            mCoordLocation = mView.findViewById(R.id.txLocation);

            mBtnUpdatePerson = mView.findViewById(R.id.btnEditPerson);
            mBtnDeletePerson = mView.findViewById(R.id.btnDeletePerson);
            mBtnShowLocation = mView.findViewById(R.id.btnLocationPerson);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "OnClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "OnLongClick", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void FirestoreInit(){
        mDatabase = FirebaseFirestore.getInstance();
    }

    private void FirestoreRefreshPerson(){
        mDatabase.collection("persons").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "error : " + e.getMessage());
                }
                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() != DocumentChange.Type.ADDED && doc.getType() != DocumentChange.Type.MODIFIED && doc.getType() != DocumentChange.Type.REMOVED) {
//                        String full_name = doc.getDocument().getString("fullname");
//                        String noktp = doc.getDocument().getString("noktp");
//                        String address = doc.getDocument().getString("address");
//                        String no_phone = doc.getDocument().getString("no_phone");
//                        String locationcoord = doc.getDocument().getString("locationcoord");

                        String personId = doc.getDocument().getId();
                        Person persons = doc.getDocument().toObject(Person.class).withId(personId);
                        if(personList.size() > 0)
                            personList.clear();


                        personList.add(persons);
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
