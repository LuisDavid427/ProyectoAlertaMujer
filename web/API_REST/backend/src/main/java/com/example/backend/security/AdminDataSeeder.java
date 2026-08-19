package com.example.backend.security;

import com.example.backend.factory.AdminFactory;
import com.example.backend.model.UsuarioModel;
import com.example.backend.model.RolModel;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class AdminDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AdminFactory adminFactory;

    public AdminDataSeeder(UsuarioRepository usuarioRepository, 
                           RolRepository rolRepository, 
                           AdminFactory adminFactory) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.adminFactory = adminFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si ya existe un usuario con correo de administrador o si hay algún usuario registrado
        Optional<UsuarioModel> adminExistente = usuarioRepository.findByEmail("admin@alertamujer.com");

        if (adminExistente.isEmpty()) {
            System.out.println("\n=================================================");
            System.out.println("   [CONFIGURACIÓN INICIAL DE ADMINISTRADOR]");
            System.out.println("=================================================");

            Scanner scanner = new Scanner(System.in);

            // 1. Pedir Nombre con reintento
            String nombre = "";
            while (nombre.isBlank()) {
                System.out.print("--> Ingrese el NOMBRE del Administrador: ");
                nombre = scanner.nextLine().trim();
                if (nombre.isBlank()) {
                    System.out.println("  [!] El nombre no puede estar vacío. Intente de nuevo.\n");
                }
            }

            // 2. Pedir Correo con reintento y validación básica de formato
            String email = "";
            while (!email.contains("@") || !email.contains(".")) {
                System.out.print("--> Ingrese el CORREO del Administrador: ");
                email = scanner.nextLine().trim();
                if (!email.contains("@") || !email.contains(".")) {
                    System.out.println("  [!] Formato de correo inválido (debe contener '@' y un dominio). Intente de nuevo.\n");
                }
            }

            // 3. Pedir Contraseña con reintento
            UsuarioModel nuevoAdmin = null;
            while (nuevoAdmin == null) {
                System.out.print("--> Ingrese la CONTRASEÑA (mínimo 6 caracteres): ");
                String rawPassword = scanner.nextLine().trim();

                try {
                    // Buscar o crear el Rol en la BD
                    RolModel rolAdmin = rolRepository.findByNombreRol("ROLE_ADMIN")
                            .orElseGet(() -> {
                                RolModel nuevoRol = new RolModel();
                                nuevoRol.setNombreRol("ROLE_ADMIN");
                                return rolRepository.save(nuevoRol);
                            });

                    // Intentar construir el objeto a través de la Fábrica
                    // Si la contraseña no cumple la regla de la fábrica, lanzará IllegalArgumentException
                    nuevoAdmin = adminFactory.crearAdmin(nombre, email, rawPassword, rolAdmin);

                } catch (IllegalArgumentException e) {
                    System.out.println("  [!] Error de validación: " + e.getMessage());
                    System.out.println("  [!] Por favor, ingrese la contraseña nuevamente.\n");
                }
            }

            // 4. Guardar en BD una vez que todos los datos son válidos
            usuarioRepository.save(nuevoAdmin);

            System.out.println("\n=================================================");
            System.out.println(" [SUCCESS] Administrador creado y cifrado con éxito.");
            System.out.println(" Email: " + nuevoAdmin.getEmail());
            System.out.println("=================================================\n");
        }
    }
}