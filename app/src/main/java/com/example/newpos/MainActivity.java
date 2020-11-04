package com.example.newpos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity {
   //Declaro variables
    itemCV item,itemOther,itemEditar;
    User user;
    String tituloEditar,subtituloEditar,primeraFechaEditar,segundaFechaEditar,habilidadEditar;
    ArrayList<itemCV> itemCVList;
    ArrayList<postulation> itemPostulations;
    User usuario;
    String auxiliar;
    int iEditItem;
    int fechas,auxiliarPostulacion;
    job jobEditar;
    FrameLayout frame;
    int configuracionActual;
    FragmentManager AdminFragments;
    String key;
    ArrayList<job> arrayAuxiliar;
    FragmentTransaction TransaccionesDeFragment;
    private FirebaseFirestore db;
    int fragment;
    View view;
    postulation postulacionItem;
    int cantEducacion,cantExp,cantSkills,cantIdi;
    int auxEducacion,auxExp,auxSkills,auxIdi;
    ArrayList<itemCV> itemsEducacion,itemsExp,itemsSkills,itemsIdi;
    BottomNavigationView bottomNav;
    TextView txtBarra;
    public String Mail,Password;
    private FirebaseAuth mAuth;
    ConstraintLayout barra;
    @Override
   //OnCreate
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bottom navbar
        auxiliarPostulacion=-1;

         bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener( navListener);
        txtBarra=findViewById(R.id.textViewBarra);
        barra=findViewById(R.id.barra);
        this.db = FirebaseFirestore.getInstance();
        AdminFragments=getFragmentManager();
        cantEducacion=0;cantExp=0;cantSkills=0;cantIdi=0;
        itemCVList=new ArrayList<>();
        itemsEducacion=new ArrayList<>();itemsExp=new ArrayList<>();itemsSkills=new ArrayList<>();itemsIdi=new ArrayList<>();
        item = new itemCV();
        usuario=new User();
        auxiliar= null;
        arrayAuxiliar=new ArrayList<job>();
        configuracionActual=1;
        mostrarInicio();
        mostrarPopUp();
        key="key";
        primeraFechaEditar=null;
        segundaFechaEditar=null;
        fechas=0;
        mostarNavBar();
        itemEditar=new itemCV();
        jobEditar=new job();
        mAuth=FirebaseAuth.getInstance();
    }

    public void mostarNavBar(){
        if(configuracionActual==1){
            bottomNav.setVisibility(View.GONE);
            barra.setVisibility(View.GONE);
        }
        if(configuracionActual==2) {
            bottomNav.setVisibility(View.VISIBLE);
            barra.setVisibility(View.VISIBLE);
        }
        }


private BottomNavigationView.OnNavigationItemSelectedListener navListener=
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String sTexto;
                Log.d("listener",item.getItemId()+"");
                switch (item.getItemId()) {

                    case R.id.nav_home:
                         sTexto=getString(R.string.Home);
                        txtBarra.setText(sTexto);
                        home();
                        break;
                    case R.id.nav_profile:
                        fragment=21;
                        traerItems();
                         sTexto=getString(R.string.My_Profile);
                        txtBarra.setText(sTexto);
                        break;
                    case R.id.nav_jobs:
                        cargarListaEmpleos();
                        sTexto=getString(R.string.Jobs);
                        txtBarra.setText(sTexto);
                        break;
                    case R.id.nav_aplicant:
                        irPostulaciones(null);
                        sTexto=getString(R.string.Postulations);
                        txtBarra.setText(sTexto);
                        break;
                    case R.id.nav_more:
                        irAcerca (null);
                        sTexto=getString(R.string.More);
                        txtBarra.setText(sTexto);
                        break;
                }
                return true;
            }
        };

    public void cargarListaEmpleos(){
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Cargando...");
        arrayAuxiliar=new ArrayList<>();
        mProgressDialog.show();
        db.collection("jobs").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("traerEmpleos", document.getId() + " => " + document.getData());
                                job unJob = new job();
                                unJob.set_jobCompany((String) document.get("jobCompany"));
                                unJob.set_jobName((String) document.get("jobName"));
                                unJob.set_jobRequired((String) document.get("jobRequired"));
                                unJob.set_jobCity((String) document.get("jobCity"));
                                unJob.set_jobAdress((String) document.get("jobAdress"));
                                unJob.set_jobDays((String) document.get("jobDays"));
                                unJob.set_jobTime((String) document.get("jobTime"));
                                unJob.set_documentPath(document.getId());
                                arrayAuxiliar.add(unJob);
                                mProgressDialog.dismiss();
                                irEmpleos();
                            }
                        } else {
                            Log.d("traerItems", "Error getting documents: ", task.getException());
                            mProgressDialog.dismiss();
                            irEmpleos();
                        }
                    }
                }); }

    //Ir a empleo
    private void irEmpleos() {
        Log.d("Fragment","Llegue");
        fragTrabajos miFragDeIngreso=new fragTrabajos();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    //Cargo inicio
    private void mostrarInicio() {
        Log.d("Fragment","Llegue");
        fragLoginsignupSiNo miFragDeIngreso=new fragLoginsignupSiNo();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=1;
    }

    //Muestra el popup
    public void mostrarPopUp(){
        fragPopUp miFragDeIngreso=new fragPopUp();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.framePrueba, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    //I a configuraciones
    public void irConfiguraciones(View view){
       if(configuracionActual==1){
           configuracionActual=2;
       }
       else if(configuracionActual==2){
           configuracionActual=1;
       }
       mostarNavBar();
        home();
    }

    //I a configuraciones
    public int obtenerConfigActual(){
        return configuracionActual;
    }

    //Nothing
    public void nothing(View view){
    }

    //Envio texto para PopUp
    public String enviarTexto(String type){
        ArrayList<itemCV>listaAux=new ArrayList<>();
        int aux=0;
        String retorno="No hay items cargados aun. ";
     for(itemCV itemFor: itemCVList){
        String s=itemFor.get_itemCVTypeId();
         if (type.equals(s)){
             listaAux.add(itemFor);
         }
     }
     if(type=="1"){
         aux=auxEducacion;
     }
     if(type=="2"){
         aux=auxExp;
     }
     if(type=="3"){
         aux=auxSkills;
     }
     if(type=="4"){
         aux=auxIdi;
     }
     if(listaAux.size()!=0){
         if(type.equals("1")||type.equals("2")) {
             retorno = listaAux.get(aux).get_itemCVTittle() + ".";
             retorno += listaAux.get(aux).get_itemCVSubTittle() + ".";
             retorno += listaAux.get(aux).get_itemCVStart() + ".";
             retorno += listaAux.get(aux).get_itemCVEnd() + ".";
         }
         if(type.equals("3")||type.equals("4")) {
             retorno = listaAux.get(aux).get_itemCVTittle() + ".";
             retorno += listaAux.get(aux).get_itemCVHabilidad() + ".";
         }
         itemOther=listaAux.get(aux);
     }
      return retorno;
    }

    //Ir siguiente item
    public void nextItem(View view){
        if(fragment==25){
            if(cantEducacion-1==auxEducacion){
                auxEducacion=0;
            }
            else{
                auxEducacion++;
            }
            item.set_itemCVTypeId("1");
        }
        if(fragment==26){
            if(cantExp-1==auxExp){
                auxExp=0;
            }
            else{
                auxExp++;
            }
            item.set_itemCVTypeId("2");
        }
        if(fragment==27){
            if(cantSkills-1==auxSkills){
                auxSkills=0;
            }
            else{
                auxSkills++;
            }
            item.set_itemCVTypeId("3");
        }
        if(fragment==28){
            if(cantIdi-1==auxIdi){
                auxIdi=0;
            }
            else{
                auxIdi++;
            }
            item.set_itemCVTypeId("4");
        }
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    //Ir siguiente item
    public void anteriorItem(View view){
        if(fragment==25){
            if(auxEducacion==0){
                auxEducacion=cantEducacion-1;
            }
            else {
                auxEducacion--;
            }
            item.set_itemCVTypeId("1");
        }
        if(fragment==26){
            if(auxExp==0){
                auxExp=cantExp-1;
            }
            else {
                auxExp--;
            }
            item.set_itemCVTypeId("2");
        }
        if(fragment==27){
            if(auxSkills==0){
                auxSkills=cantSkills-1;
            }
            else {
                auxSkills--;
            }
            item.set_itemCVTypeId("3");
        }
        if(fragment==28){
            if(auxIdi==0){
                auxIdi=cantIdi-1;
            }
            else {
                auxIdi--;
            }
            item.set_itemCVTypeId("4");
        }
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    //Cargo el login de username
    public void login(View view){
        Log.d("Fragment","Llegue");
        fragLogin1 miFragDeIngreso=new fragLogin1();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=2;
    }

    //Cargo el login de password
    public void login1(View view){

        Log.d("Fragment","Llegue");
        fragLogin2 miFragDeIngreso=new fragLogin2();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=3;
    }

    public void enviarMail(String mail){
       Mail=mail;
       Log.d("enviarDatosUser",Mail);
    }

    public void enviarPassword(String password){
        Password=password;
        Log.d("enviarDatosUser",Password);
        if(Mail.isEmpty()&&Password.isEmpty()){
            Log.d("enviarDatosUser","No se cargo");
            if (configuracionActual==1){
                login(null);
            }
            if (configuracionActual==2){
                mostrarInicio();
            }
        }
        else{
            iniciarSesion();
        }
    }

    private void iniciarSesion() {
        mAuth.signInWithEmailAndPassword(Mail,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("iniciarSesion", "Inicio de Sesion completado");
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
//Hide:
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    traerUsuario();
                }
            }
        });
    }

    public void traerUsuario(){
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Cargando...");
        mProgressDialog.show();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("user").whereEqualTo("id", currentuser).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("traerUsuario", document.getId() + " => " + document.getData());
                                 user = new User();
                                auxiliar=document.getId();
                                user.set_userName((String) document.get("userName"));
                                user.set_userLastName((String) document.get("userLastname"));

                                mProgressDialog.dismiss();
                                irHome(null);
                                if (configuracionActual==2){
                                    mostarNavBar();
                                    bottomNav.setOnNavigationItemSelectedListener(navListener);
                                    bottomNav.setSelectedItemId(R.id.nav_home);
                                }

                            }
                        } else {
                            Log.d("traerItems", "Error getting documents: ", task.getException());
                            mProgressDialog.dismiss();
                        }
                        analizarItems();
                    }
                });
    }

    public User enviarUsuario(){
        return user;
    }

    //Cargo el fragment de registro
    public void register1(View view){
        Log.d("Fragment","Llegue");
        fragRegister1 miFragDeIngreso=new fragRegister1();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=4;
    }

    //Cuando se clickea el continuar en el registro
    public void loginContinue(View view){
        //Si llego al final
       if(fragment==19){
           home();
       }
        //Normalmente
        else if(fragment!=16){
            Log.d("Fragment","Llegue");
            fragRegister1 miFragDeIngreso=new fragRegister1();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            fragment++;
        }
        //Fragment de si tiene o no registro
        else {
            fragRegister2 miFragDeIngreso=new fragRegister2();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            fragment=17;
        }
    }

    //Si tiene registro
    public void tieneRegistro(View view)
    {
        fragRegister1 miFragDeIngreso=new fragRegister1();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=18;
    }

    //Si no tiene registro
    public void noTieneRegistro(View view)
    {
        fragRegister1 miFragDeIngreso=new fragRegister1();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=19;
    }

    //onClick que va a la home
    public void irHome(View view)
    {
        home();
        fragment=20;
    }

    //Ir a home
    public void home(){
    fragHome miFragDeIngreso=new fragHome();
    TransaccionesDeFragment=AdminFragments.beginTransaction();
    TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
    TransaccionesDeFragment.commit();
    String sTexto=getString(R.string.Home);
    txtBarra.setText(sTexto);
    }

    public ArrayList<itemCV> obtenerListaEducacion(){
        return itemsEducacion;
    }
    public ArrayList<itemCV> obtenerListaExperiencia(){
        return itemsExp;
    }
    public ArrayList<itemCV> obtenerListaSkills(){
        return itemsSkills;
    }
    public ArrayList<itemCV> obtenerListaIdioma(){
        return itemsIdi;
    }
    public ArrayList<job> obtenerListaEmpleos(){
        return arrayAuxiliar;
    }




    //Ir a la funcion para ir a ver mi perfil
    public void irPerfil(View view){
        if(configuracionActual==1){
            traerItems();
        }
        perfil();
    }
    //Ir a mi perfil
    public void perfil(){
        fragVerPerfilCv miFragDeIngreso=new fragVerPerfilCv();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=21;
    }
    //Ver perfil
    public void verPerfil(View view){
        fragVerPerfil miFragDeIngreso=new fragVerPerfil();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=22;
    }

    //Ver CV
    public void verCV(View view){
        fragVerCV miFragDeIngreso=new fragVerCV();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=23;
    }

    //Traer items de la Base de Datos
    public void traerItems(){
        itemCVList=new ArrayList<>();
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Cargando...");
        mProgressDialog.show();
        db.collection("itemCV").whereEqualTo("id", auxiliar).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("traerItems", document.getId() + " => " + document.getData());
                                itemCV item = new itemCV();
                                String aux=(String) document.get("itemCVTypeId");
                                item.set_itemCVTypeId((String) document.get("itemCVTypeId"));
                                if("1".equals(aux)||"2".equals(aux)){
                                   item.set_itemCVTittle((String) document.get("itemCVTittle"));
                                   item.set_itemCVSubTittle((String) document.get("itemCVSubTittle"));
                                   item.set_itemCVEnd((String) document.get("itemCVEnd"));
                                   item.set_itemCVStart((String) document.get("itemCVStart"));
                               }
                                if("3".equals(aux)||"4".equals(aux)){
                                    item.set_itemCVTittle((String) document.get("itemCVTittle"));
                                    item.set_itemCVHabilidad((String)document.get("itemCVHabilidad"));
                                }
                                item.set_documentPath(document.getId());
                                itemCVList.add(item);
                                mProgressDialog.dismiss();
                            }
                        } else {
                            Log.d("traerItems", "Error getting documents: ", task.getException());
                            mProgressDialog.dismiss();
                        }
                        analizarItems();
                    }
                });
    }

    //Analizar items
    public void analizarItems(){
        cantEducacion=0;cantExp=0;cantSkills=0;cantIdi=0;
        itemsEducacion=new ArrayList<>();itemsExp=new ArrayList<>(); itemsSkills=new ArrayList<>();itemsIdi=new ArrayList<>();
        for(itemCV itemFor: itemCVList) {
                String s=itemFor.get_itemCVTypeId();
                if ("1".equals(s)){ cantEducacion++; itemsEducacion.add(itemFor);}
                if ("2".equals(s)){ cantExp++; itemsExp.add(itemFor);}
                if ("3".equals(s)){ cantSkills++; itemsSkills.add(itemFor);}
                if ("4".equals(s)){ cantIdi++; itemsIdi.add(itemFor);}
        }
        Log.d("analizarItems", String.valueOf(cantEducacion)+"-"+String.valueOf(cantExp)+"-"+String.valueOf(cantSkills)+"-"+String.valueOf(cantIdi));
        auxEducacion=0;auxExp=0;auxSkills=0;auxIdi=0;
        if(fragment==34||fragment==321||fragment==322||fragment==30||fragment==301||fragment==302){
            fragEditCV miFragDeIngreso=new fragEditCV();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            mostarNavBar();
            fragment=24;
        }

        if(fragment==21){
            perfil();
        }

            if(fragment==31||fragment==29){
                fragEditItem miFragDeIngreso=new fragEditItem();
                TransaccionesDeFragment=AdminFragments.beginTransaction();
                TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                TransaccionesDeFragment.commit();
                fragment=25;
            }
            if(fragment==311||fragment==291){
                fragEditItem miFragDeIngreso=new fragEditItem();
                TransaccionesDeFragment=AdminFragments.beginTransaction();
                TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                TransaccionesDeFragment.commit();
                fragment=26;
            }
            if(fragment==312||fragment==292){
                fragEditItem miFragDeIngreso=new fragEditItem();
                TransaccionesDeFragment=AdminFragments.beginTransaction();
                TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                TransaccionesDeFragment.commit();
                fragment=27;
            }
            if(fragment==313||fragment==293){
                fragEditItem miFragDeIngreso=new fragEditItem();
                TransaccionesDeFragment=AdminFragments.beginTransaction();
                TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                TransaccionesDeFragment.commit();
                fragment=28;
            }
    }


    public void recibirEmpleo(String path){
        Log.d("recibirEmpleo","Se pulso el empleo "+path);
        for (int i=0;i<arrayAuxiliar.size();i++){
            job unJob=arrayAuxiliar.get(i);
            if(unJob.get_documentPath()==path){
                jobEditar=unJob;
                i=itemCVList.size()+1;
            }
        }


//ACA IRIA FRAGMENT
        fragTrabajoSeleccionado miFragDeIngreso=new fragTrabajoSeleccionado();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        ImageView flecha =findViewById(R.id.flecha);flecha.setVisibility(View.VISIBLE);
        fragment=777;
    }

    public void recibirPostulaciones(String path) {
        Log.d("recibirPostulaciones", "Se pulso la postulacion " + path);
        key=path;
        for (int i = 0; i < itemPostulations.size(); i++) {
            postulation unPostulation = itemPostulations.get(i);
            if (unPostulation.get_documentPath() == path) {
                postulacionItem = unPostulation;
                Log.d("recibirPostulaciones", "" + postulacionItem.get_jobTime());
                if(unPostulation.get_postulationStatus().equals("0")){
                    auxiliarPostulacion=0;
                }
                else if(unPostulation.get_postulationStatus().equals("1")){
                    auxiliarPostulacion=1;
                }
                else if(unPostulation.get_postulationStatus().equals("2")){
                    auxiliarPostulacion=2;
                }
                i = itemPostulations.size() + 1;
            }
        }
        //ACA IRIA FRAGMENT
        fragPostulacionPulsada miFragDeIngreso=new fragPostulacionPulsada();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        ImageView flecha =findViewById(R.id.flecha);flecha.setVisibility(View.VISIBLE);
        fragment=888;
        if(auxiliarPostulacion==1) {
            final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
            konfettiView.build()
                    .addColors(Color.BLUE, Color.RED, Color.WHITE)
                    .setDirection(0.0, 359.0)
                    .setSpeed(3f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                    .streamFor(300, 1000L);
        }
        }

    public int enviarAuxiliarPostulacion(){
        return auxiliarPostulacion;
    }

    public postulation enviarPostulacionItem(){
        return postulacionItem;
    }

    public void recibirPath(String path){
        Log.d("recibirPath","Lo recibi: "+path);
        for (int i=0;i<itemCVList.size();i++){
           itemCV unItem=itemCVList.get(i);
            if(unItem.get_documentPath()==path){
                itemEditar=unItem;
                i=itemCVList.size();
            }
        }
        Log.d("recibirPath","Vamo a editar ");
        bottomNav.setVisibility(View.GONE);
        fragment=30;
        fragEditItemIntelectual miFragDeIngreso=new fragEditItemIntelectual();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    //Enviar item editar
    public String enviarItemEditar(){
        String unItem="";
        if(fragment==25||fragment==26) {
            unItem += itemOther.get_itemCVTittle() + ".";
            unItem += itemOther.get_itemCVSubTittle() + ".";
            unItem += itemOther.get_itemCVStart() + " a ";
            unItem += itemOther.get_itemCVEnd() + ". ";
        }
        if(fragment==292||fragment==293) {
            unItem += itemOther.get_itemCVTittle() + ".";
            unItem += itemOther.get_itemCVHabilidad() + ".";
        }
        key=itemOther.get_documentPath();
        Log.d("enviarItemEditar",unItem);
        return unItem;
    }

    //Enviar cv
    public String enviarCV(){
        int aux=0;
        String retorno= "No hay CV cargado aun";
        if(itemCVList.size()!=0){
        retorno="Educacion: ";
        if(cantEducacion!=0) {
            for (itemCV itemFor : itemsEducacion) {
                aux++;
                retorno += "Item " + aux+": ";
                retorno += itemFor.get_itemCVTittle() + ".";
                retorno += itemFor.get_itemCVSubTittle() + ".";
                retorno += itemFor.get_itemCVStart() + " a ";
                retorno += itemFor.get_itemCVEnd() + ". ";
            }
        }
        else{
            retorno+="No hay items de educacion cargados aun";
        }
            retorno+="Experiencia laboral: ";
            if(cantExp!=0){
                aux=0;
                for(itemCV itemFor: itemsExp){
                    aux++;
                    retorno+="Item "+aux+": ";
                    retorno+= itemFor.get_itemCVTittle()+".";
                    retorno+=itemFor.get_itemCVSubTittle()+".";
                    retorno+=itemFor.get_itemCVStart()+" a ";
                    retorno+=itemFor.get_itemCVEnd()+". ";
                }
            }
            else{
                retorno+="No hay items de experiencia laboral cargados aun. ";
            }

            retorno+="Skills: ";

            if(cantSkills!=0){
                aux=0;
                for(itemCV itemFor: itemsSkills){
                    aux++;
                    retorno+="Item "+aux+": ";
                    retorno+= itemFor.get_itemCVTittle()+".";
                    retorno+=itemFor.get_itemCVHabilidad()+".";
                }
            }
            else{
                retorno+="No hay items de skills cargados aun. ";
            }
            retorno+="Idioma: ";
            if(cantIdi!=0){
                aux=0;
                for(itemCV itemFor: itemsIdi){
                    aux++;
                    retorno+="Item "+aux+": ";
                    retorno+= itemFor.get_itemCVTittle()+".";
                    retorno+=itemFor.get_itemCVHabilidad()+".";
                }
            }
            else{
                retorno+="No hay items de idioma cargados aun. ";
            }
        }
        Log.d("enviarCV",retorno);
        return retorno;
    }

    //Editar CV
    public void editarCV(View view){
        fragEditCV miFragDeIngreso=new fragEditCV();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=24;
    }

    public void back(View view){
        Log.d("fragment",fragment+"");
        if (fragment==24){
            perfil();
            ImageView flecha =findViewById(R.id.flecha);flecha.setVisibility(View.GONE);
            String sTexto=getString(R.string.My_Profile);
            txtBarra.setText(sTexto);
        }
        if (fragment==30||fragment==301||fragment==302){
            editarCV(null);
            mostarNavBar();
        }
        if (fragment==777){
            ImageView flecha =findViewById(R.id.flecha);flecha.setVisibility(View.GONE);
            fragTrabajos miFragDeIngreso=new fragTrabajos();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
        }
        if (fragment==888){
            ImageView flecha =findViewById(R.id.flecha);flecha.setVisibility(View.GONE);
            fragPostulacionesPendientesAceptadas miFragDeIngreso=new fragPostulacionesPendientesAceptadas();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
        }
        if (fragment==37){
            fragment=777;
            fragTrabajoSeleccionado miFragDeIngreso=new fragTrabajoSeleccionado();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
        }
    }

public ArrayList<itemCV> traerLista(){
        return itemCVList;
}

public void editarItemIntelectual(itemCV item){
    DocumentReference dato = db.collection("itemCV").document(item.get_documentPath());
    // Set the "isCapital" field of the city 'DC'
    dato
            .update("itemCVTittle", item.get_itemCVTittle())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("saveUno", "DocumentSnapshot successfully updated!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("saveUno", "Error actualizando el documento", e);
                }
            });
    if(item.get_itemCVTypeId().equals("1")||item.get_itemCVTypeId().equals("2")) {
        dato
            .update("itemCVStart", item.get_itemCVStart())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
        dato
                .update("itemCVEnd", item.get_itemCVEnd())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
        dato
                .update("itemCVSubTittle", item.get_itemCVSubTittle())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
    }
    else{
        dato
                .update("itemCVHabilidad", item.get_itemCVHabilidad())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
    }
}

public void cancelar(){
        Log.d("Cancelar","Se cancela");
    editarCV(null);
    mostarNavBar();
}

    public void eliminar(String path){
        Log.d("eliminar","Se va a eliminar");
        db.collection("itemCV").document(path)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("eliminarItem", "Documento eliminado correctamente.");
                        Log.d("eliminarItem", ""+fragment);
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("eliminarItem", "Error al eliminar el documento", e);
                    }
                });
    }

    //Editar educacion
    public void editarEducacion(View view){
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        item.set_itemCVTypeId("1");
        fragment=25;
    }

    //Editar Experincia Laboral
    public void editarExperinciaLaboral(View view){
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        item.set_itemCVTypeId("2");
        fragment=26;
    }

    //Editar Skills
    public void editarSkills(View view){
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        item.set_itemCVTypeId("3");
        fragment=27;
    }

    //Editar Idiomas
    public void editarIdiomas(View view){
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        item.set_itemCVTypeId("4");
        fragment=28;
    }

    public void irEditarCV(View view){

    }

    //Editar Item
    public void editItem(View view){

        if(fragment==25&&cantEducacion!=0)
        {
            editItemEducacion();
        }
        if(fragment==26&&cantExp!=0)
        {
            editItemExpLab();
        }
        if(fragment==27&&cantSkills!=0)
        {
            editItemSkills();
        }
        if(fragment==28&&cantIdi!=0)
        {
            editItemIdioma();
        }
        }

    //Editar Item De Educacion
    public void editItemEducacion(){
        fragEditItemEducacion miFragDeIngreso=new fragEditItemEducacion();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=29;
    }

    //Editar Item De Experiencia Laboral
    public void editItemExpLab(){
        fragEditItemEducacion miFragDeIngreso=new fragEditItemEducacion();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=291;
    }

    //Editar Item De Skills
    public void editItemSkills(){
        fragEditItem2 miFragDeIngreso=new fragEditItem2();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=292;
    }

    public itemCV enviarItemEditarIntelectual(){
        return itemEditar;
    }

    public job enviarEmpleo(){
        return jobEditar;
    }


    //Editar Item De Idioma
    public void editItemIdioma(){
        fragEditItem2 miFragDeIngreso=new fragEditItem2();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=293;
    }

public void addItemIntelectual(View view){
    Log.d("vamoEditar","Llegue");
    TextView texto=view.findViewById(R.id.texto);
    String s=texto.getText().toString();
    Log.d("vamoEditar",s);
    if(s==(getString(R.string.AddEstudios))){
        item.set_itemCVTypeId("1");
        fragment=30;
    }
    if(s==(getString(R.string.AddExperience))){
        item.set_itemCVTypeId("2");
        fragment=30;
    }
    if(s==(getString(R.string.AddSkill))){
        fragment=301;
        item.set_itemCVTypeId("3");
    }
    if(s==(getString(R.string.AddIdioma))){
        item.set_itemCVTypeId("4");
        fragment=302;
    }
    fragAddItem miFragDeIngreso=new fragAddItem();
    TransaccionesDeFragment=AdminFragments.beginTransaction();
    TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
    TransaccionesDeFragment.commit();
    bottomNav.setVisibility(View.GONE);
    txtBarra.setText(getString(R.string.Add));
}

public void agregarItemIntelectual (View view){
    Log.d("agregarItemIntelectual","Vamos a declarar");
    EditText txtTitulo=view.findViewById(R.id.editUno);
    EditText txtFechaInicio=view.findViewById(R.id.editDos);
    EditText txtFechaFinal=view.findViewById(R.id.editTres);
    EditText txtSubTitulo=view.findViewById(R.id.editCuatro);
    Log.d("agregarItemIntelectual","Cargamos datos");
    item.set_itemCVTittle(txtTitulo.getText().toString());
    item.set_itemCVStart(txtFechaInicio.getText().toString());
    item.set_itemCVEnd(txtFechaFinal.getText().toString());
    item.set_itemCVSubTittle(txtSubTitulo.getText().toString());
    Log.d("agregarItemIntelectual","A subir");
    subirItem();
}

    //Añadir Items
    public void addItem(View view){
        fragAddItem miFragDeIngreso=new fragAddItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();

        if(item.get_itemCVTypeId()=="1"||item.get_itemCVTypeId()=="2"){
            fragment=30;
        }
        if(item.get_itemCVTypeId()=="3"){
            fragment=301;
        }
        if(item.get_itemCVTypeId()=="4"){
            fragment=302;
        }
    }

    //Volver del Editar
    public void backItem(View view){

        if(fragment==29)
        {
            fragment=25;
        }
        if(fragment==291)
        {
            fragment=26;
        }
        if(fragment==292)
        {
            fragment=27;
        }
        if(fragment==293)
        {
            fragment=28;
        }
        fragEditItem miFragDeIngreso=new fragEditItem();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
    }

    public int devolerEditor(){
        return iEditItem;
    }

     //Editar Name
    public void editName(View view){
        if(fragment==29){
            fragment=31;
        }
        if(fragment==291){
            fragment=311;
        }
        if(fragment==292){
            fragment=312;
        }
        if(fragment==293){
            fragment=313;
        }
        fragEdit miFragDeIngreso=new fragEdit();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        iEditItem=1;
    }

    //Editar fechas
    public void editDates(View view){
        if(fragment==29){
            fragment=31;
        }
        if(fragment==291){
            fragment=311;
        }
        fragEdit miFragDeIngreso=new fragEdit();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        iEditItem=2;
    }


    //Editar descripcion
    public void editDescripcion(View view){
        if(fragment==29){
            fragment=31;
        }
        if(fragment==291){
            fragment=311;
        }
        fragEdit miFragDeIngreso=new fragEdit();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        iEditItem=3;
    }

    //Editar habilidad
    public void editHability(View view){
if(fragment==292){fragment=312;}
        if(fragment==293){fragment=313;}
        fragEdit miFragDeIngreso=new fragEdit();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        iEditItem=4;
    }
    //Devolver Titulo
    public String enviarTituloItemEditar(){
        return itemOther.get_itemCVTittle();
    }

    //Devolver Titulo
    public String enviarTypeItem(){
        return item.get_itemCVTypeId();
    }

    //Devolver primera fecha
    public String enviarPrimeraFechaItemEditar(){
        return itemOther.get_itemCVStart();
    }

    //Devolver segunda fecha
    public String enviarSegundaFechaItemEditar(){
        return itemOther.get_itemCVEnd();
    }


    //Devolver subtitulo
    public String enviarSubTituloItemEditar(){
        return itemOther.get_itemCVSubTittle();
    }

    //Devolver habilidad
    public String enviarHabiliadItemEditar(){
        return itemOther.get_itemCVHabilidad();
    }

    //Cancelar edicion
    public void cancelEdit(View view){

        if(fragment==31){
            fragEditItem miFragDeIngreso=new fragEditItem();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            fragment=25;
        }
    }

    //Continuar con el ingreso de un item nuevo al CV
    public void addItemContinue(){
        if(fragment==30||fragment==301||fragment==302){
            fragAddItem miFragDeIngreso=new fragAddItem();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            if(item.get_itemCVTypeId()=="1"||item.get_itemCVTypeId()=="2"){
                fragment=32;
            }
            if(item.get_itemCVTypeId()=="3"){
                fragment=321;
            }
            if(item.get_itemCVTypeId()=="4"){
                fragment=322;
            }
        }
        else{

            if(fragment==34||fragment==321||fragment==322){
                subirItem();
            }
            else{
                fragment++;
                fragAddItem miFragDeIngreso=new fragAddItem();
                TransaccionesDeFragment=AdminFragments.beginTransaction();
                TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                TransaccionesDeFragment.commit();
            }
        }
    }

    public void ObtenerTitulo(String titulo)
    {
        item.set_itemCVTittle(titulo);
        Log.d("ingresoItem",titulo);
    }

    public void ObtenerInicio(String start)
    {
        item.set_itemCVStart(start);
        Log.d("ingresoItem",start);
    }
    public void ObtenerFinal(String end)
    {
        item.set_itemCVEnd(end);
        Log.d("ingresoItem",end);
    }
    public void ObtenerSubtitulo(String subttitle)
    {
        item.set_itemCVSubTittle(subttitle);
        Log.d("ingresoItem",subttitle);
    }
    public void ObtenerNivelHabilidad(String nivelHabilidad)
    {
        item.set_itemCVHabilidad(nivelHabilidad);
        Log.d("ingresoItem",nivelHabilidad);
    }


    public void subirItem(){
        Log.d("fragment",fragment+"");
        Map<String, Object> data = new HashMap<>();
        data.put(("id"),auxiliar);
        if(item.get_itemCVTypeId()=="1"||item.get_itemCVTypeId()=="2"){
        data.put("itemCVTypeId", item.get_itemCVTypeId());
        data.put("itemCVTittle", item.get_itemCVTittle());
        data.put("itemCVStart", item.get_itemCVStart());
        data.put("itemCVEnd", item.get_itemCVEnd());
        data.put("itemCVSubTittle", item.get_itemCVSubTittle());
        }
        if(item.get_itemCVTypeId()=="3"||item.get_itemCVTypeId()=="4"){
            data.put("itemCVTittle", item.get_itemCVTittle());
            data.put("itemCVTypeId", item.get_itemCVTypeId());
            data.put("itemCVHabilidad", item.get_itemCVHabilidad());
        }

        db.collection("itemCV")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("AgregarItem", "Perfecto");
                        Toast.makeText(getApplicationContext(), "Item agregado correctamente!", Toast.LENGTH_SHORT).show();
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AgregarItem", "Error al añadir el item", e);
                        Toast.makeText(getApplicationContext(), "Ocurrio un error y no se pudo agregar el item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Eliminar item
    public void eliminarItem(View view){
        db.collection("itemCV").document(key)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("eliminarItem", "Documento eliminado correctamente.");
                        Log.d("eliminarItem", ""+fragment);
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("eliminarItem", "Error al eliminar el documento", e);
                    }
                });
    }

    //Eliminar Postulacion
    public void eliminarPostulacion(View view){
        db.collection("userPostulations").document(key)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("eliminarItem", "Documento eliminado correctamente.");
                        irPostulaciones(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("eliminarItem", "Error al eliminar el documento", e);
                    }
                });
    }


    //Va  al fragment de Jobs
        public void irTrabajo (View view)
        {
            fragTrabajo miFragDeIngreso=new fragTrabajo();
            TransaccionesDeFragment=AdminFragments.beginTransaction();
            TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
            TransaccionesDeFragment.commit();
            fragment=35;
        }


        //Para ver mas informacion de empleos
    public void moreInformation (View view)
    {
        fragMoreInfo miFragDeIngreso=new fragMoreInfo();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=36;
    }

    //Para postularse a un empleo
    public void Postular (View view)
    {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Cargando...");
        mProgressDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put(("userID"),auxiliar);
        data.put(("jobCompany"),jobEditar.get_jobCompany());
        data.put(("jobAdress"),jobEditar.get_jobAdress());
        data.put(("jobCity"),jobEditar.get_jobCity());
        data.put(("jobDays"),jobEditar.get_jobDays());
        data.put(("jobRequired"),jobEditar.get_jobRequired());
        data.put(("jobTime "),jobEditar.get_jobTime());
        data.put(("jobName"),jobEditar.get_jobName());
        data.put(("companyMail"),"");
        data.put(("companyMensaje"),"");
        data.put(("postulationStatus"),"0");
        data.put(("jobPath"),jobEditar.get_documentPath());

        db.collection("userPostulations")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("AgregarItem", "Perfecto");
                        Toast.makeText(getApplicationContext(), "Postulacion agregada correctamente!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        fragPostulaciones miFragDeIngreso=new fragPostulaciones();
                        TransaccionesDeFragment=AdminFragments.beginTransaction();
                        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
                        TransaccionesDeFragment.commit();
                        fragment=37;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AgregarItem", "Error al añadir el item", e);
                        Toast.makeText(getApplicationContext(), "Ocurrio un error y no se pudo agregar el item", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
    }

    //Para ver todas tus postulaciones
    public void irPostulaciones (View view)
    {
        itemPostulations=new ArrayList<>();
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Cargando...");
        mProgressDialog.show();
        db.collection("userPostulations").whereEqualTo("userID", auxiliar).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("traerItems", document.getId() + " => " + document.getData());
                                postulation unaPostulation = new postulation();
                                unaPostulation.set_jobCompany((String) document.get("jobCompany"));
                                unaPostulation.set_jobName((String) document.get("jobName"));
                                unaPostulation.set_jobRequired((String) document.get("jobRequired"));
                                unaPostulation.set_jobCity((String) document.get("jobCity"));
                                unaPostulation.set_jobAdress((String) document.get("jobAdress"));
                                unaPostulation.set_jobDays((String) document.get("jobDays"));
                                unaPostulation.set_jobTime((String) document.get("jobTime"));
                                unaPostulation.set_companyMail((String) document.get("companyMail"));
                                unaPostulation.set_companyMensaje((String) document.get("companyMensaje"));
                                unaPostulation.set_userID((String)document.get("userID"));
                                unaPostulation.set_postulationStatus((String)document.get("postulationStatus"));
                                unaPostulation.set_documentPath(document.getId());
                                itemPostulations.add(unaPostulation);
                                mProgressDialog.dismiss();
                            }
                            irVistaPostular();
                        } else {
                            Log.d("traerItems", "Error getting documents: ", task.getException());
                            mProgressDialog.dismiss();
                            irVistaPostular();
                        }
                    }
                });
    }

    public void irVistaPostular(){
        fragPostulacionesPendientesAceptadas miFragDeIngreso=new fragPostulacionesPendientesAceptadas();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=38;
    }

    //Para ver postulaciones pendientes
    public void PostulacionesPenidentes (View view)
    {
        fragPostulacionesPendientes miFragDeIngreso=new fragPostulacionesPendientes();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=39;
    }

    //Para ver postulaciones aceptadas
    public void PostulacionesAceptadas (View view)
    {
        fragNoPostulacionAceptada miFragDeIngreso=new fragNoPostulacionAceptada();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=40;
    }

    //Para ir a acerca
    public void irAcerca (View view)
    {
        fragAcerca miFragDeIngreso=new fragAcerca();
        TransaccionesDeFragment=AdminFragments.beginTransaction();
        TransaccionesDeFragment.replace(R.id.FrameParaFragmentIngreso, miFragDeIngreso);
        TransaccionesDeFragment.commit();
        fragment=41;
    }

    //Para cerrar sesion
    public void LogOut (View view)
    {
        Log.d("logOut","Vamo a cerrar sesion");
        mAuth.signOut();
        bottomNav.setVisibility(View.GONE);
        barra.setVisibility(View.GONE);
        mostrarInicio();
    }

    //Funcion para enviar la lista de postulaciones
    public ArrayList<postulation> traerPostulaciones(){
        return itemPostulations;
    }

    //Funcion para devolver el valor del fragment, osea que fragment estamos.
    public int devolverFragment(){
        return fragment;
    }

    //Recibo el titulo editado
    public void enviarTituloEditar(String titulo){
        tituloEditar=titulo;
    }

    //Recibo primera fecha editado
    public void enviarPrimeraFecha(String fecha){
        primeraFechaEditar=fecha; Log.d("fechas",primeraFechaEditar);
    }

    //Recibo segunda fecha editado
    public void enviarSegundaFecha(String fecha){
        segundaFechaEditar=fecha;Log.d("fechas",segundaFechaEditar);
    }

    //Recibo el Sub titulo editado
    public void enviarSubTituloEditar(String subTitulo){
        subtituloEditar=subTitulo;
    }

    //Recibo el nivel de habilidad editado
    public void enviarHabilidadEditar(String subTitulo){
        habilidadEditar=subTitulo;
    }

    //Guardar Edicion
    public void saveEdit(){
        if(fragment==31){
            if(iEditItem==1){
                saveUno();
            }
            if(iEditItem==2&&fechas==1){
                saveDos();
            }
            if(iEditItem==3){
                saveUno();
            }
        }
        if(fragment==311){
            if(iEditItem==1){
                saveUno();
            }
            if(iEditItem==2&&fechas==1){
                saveDos();
            }
            if(iEditItem==3){
                saveUno();
            }
        }
        if(fragment==312){
            saveUno();
        }
        if(fragment==313){
                saveUno();
        }
    }

    //Editar de tipo 1
    public void saveUno(){
        String texto= "";
        String field="";
        if(iEditItem==1){texto=tituloEditar;field="itemCVTittle";}
        if(iEditItem==2){}
        if(iEditItem==3){texto=subtituloEditar;field="itemCVSubTittle";}
        if(iEditItem==4){texto=habilidadEditar;field="itemCVHabilidad";}
        DocumentReference dato = db.collection("itemCV").document(key);
        // Set the "isCapital" field of the city 'DC'
        dato
                .update(field, texto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
    }

    //Editar de tipo 2
    public void saveDos(){
        DocumentReference dato = db.collection("itemCV").document(key);
        dato
                .update("itemCVStart", primeraFechaEditar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                        if(fechas==1){traerItems();}
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
        dato
                .update("itemCVEnd", segundaFechaEditar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUno", "DocumentSnapshot successfully updated!");
                        traerItems();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saveUno", "Error actualizando el documento", e);
                    }
                });
        fechas=0;
    }


    //Modificar fechas
    public void modificarFechas(){
        fechas++;
        Log.d("modificarFechas",fechas+"");
    }

    //Enviar PosicionFecha
    public int enviarPosicionFecha(){
        Log.d("enviarPosicionFecha",fechas+"");
        return fechas;
    }
}
