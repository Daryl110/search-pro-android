package Modelo;

public class Usuario {
    private String nombreUsuario, correo, password;

    public Usuario(String nombreUsuario, String correo, String password) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.password = password;
    }

    @Override
    public String toString(){
        return "[Usuario] : "+this.nombreUsuario;
    }
}