package com.example.gimnasio_unne.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gimnasio_unne.R;

public class FragmentMiPerfil extends Fragment {

    public FragmentMiPerfil() {   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mi_perfil, container, false);
    }
}