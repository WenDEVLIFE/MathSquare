package com.happym.mathsquare.Service;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDb {

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance("mathsquare-research-db");
    }
}