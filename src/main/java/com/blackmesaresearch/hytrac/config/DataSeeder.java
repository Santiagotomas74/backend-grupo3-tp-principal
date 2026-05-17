package com.blackmesaresearch.hytrac.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.blackmesaresearch.hytrac.dto.csv.CombustibleCsv;
import com.blackmesaresearch.hytrac.dto.csv.LocalidadCsv;
import com.blackmesaresearch.hytrac.dto.csv.LugarOperativoCsv;
import com.blackmesaresearch.hytrac.dto.csv.ProvinciaCsv;
import com.blackmesaresearch.hytrac.dto.csv.UsuarioCsv;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.lookup.Permiso;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.lookup.TipoLugarOperativo;
import com.blackmesaresearch.hytrac.model.reference.*;
import com.blackmesaresearch.hytrac.repository.*;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProvinciaRepository provinciaRepo;
    private final LocalidadRepository localidadRepo;
    private final CombustibleRepository combustibleRepo;
    private final RolRepository rolRepo;
    private final PermisoRepository permisoRepo;
    private final LugarOperativoRepository lugarRepo;
    private final TipoLugarOperativoRepository tipoLugarOperativoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CsvMapper csvMapper = new CsvMapper();

    public DataSeeder(
            ProvinciaRepository provinciaRepo,
            LocalidadRepository localidadRepo,
            CombustibleRepository combustibleRepo,
            RolRepository rolRepo,
            PermisoRepository permisoRepo,
            LugarOperativoRepository lugarRepo,
            TipoLugarOperativoRepository tipoLugarOperativoRepo,
            UsuarioRepository usuarioRepo) {
        this.provinciaRepo = provinciaRepo;
        this.localidadRepo = localidadRepo;
        this.combustibleRepo = combustibleRepo;
        this.rolRepo = rolRepo;
        this.permisoRepo = permisoRepo;
        this.lugarRepo = lugarRepo;
        this.tipoLugarOperativoRepo = tipoLugarOperativoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Flujo principal - Define el orden en el que se cargan las tablas
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed if the database is empty (since you nuke the DB often)
        if (provinciaRepo.count() > 0)
            return;

        log.info("Starting CSV Seeding...");

        // 1. Load Provincias (returns Map for Localidades to use)
        Map<String, Provincia> provinciaMap = loadProvincias();

        // 2. Load Localidades (now returns Map for Lugares Operativos to use)
        Map<String, Localidad> localidadMap = loadLocalidades(provinciaMap);

        // 3. Load Combustibles
        loadCombustibles();

        // 4. Load Roles
        Map<String, Rol> rolMap = loadRoles();

        // 5. Load Permissions
        Map<String, Permiso> permisoMap = loadPermisos();

        // 6. Load Rol-Permission relationships
        loadRolPermisos(rolMap, permisoMap);

        // 7. Load Lugares Operativos
        Map<String, TipoLugarOperativo> tipoLugarMap = tipoLugarOperativoRepo.findAll()
                .stream().collect(Collectors.toMap(TipoLugarOperativo::getNombre, t -> t));

        // Capture the map return from loadLugaresOperativos
        Map<String, LugarOperativo> lugarMap = loadLugaresOperativos(tipoLugarMap, localidadMap);

        // 8. Load Users
        loadUsuarios(rolMap, lugarMap);

        log.info("Full database seeding completed successfully!");

        log.info("Seeding complete!");
    }

    private Map<String, Provincia> loadProvincias() throws IOException {
        InputStream is = new ClassPathResource("seed/provincias.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<ProvinciaCsv> it = csvMapper.readerFor(ProvinciaCsv.class)
                .with(schema).readValues(is);

        List<Provincia> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            Provincia p = new Provincia();
            p.setNombre(row.getNombre());
            saved.add(provinciaRepo.save(p));
        });

        // Create a lookup map: "Buenos Aires" -> Provincia Object (with its new ID)
        return saved.stream().collect(Collectors.toMap(Provincia::getNombre, p -> p));
    }

    private Map<String, Localidad> loadLocalidades(Map<String, Provincia> provinciaMap) throws IOException {
        InputStream is = new ClassPathResource("seed/localidades.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<LocalidadCsv> it = csvMapper.readerFor(LocalidadCsv.class)
                .with(schema).readValues(is);

        it.forEachRemaining(row -> {
            Localidad loc = new Localidad();
            loc.setNombre(row.getNombre());
            loc.setCodigoPostal(row.getCodigo_postal());

            // This is the magic: link by name from our map
            Provincia parent = provinciaMap.get(row.getProvincia_nombre());
            if (parent != null) {
                loc.setProvincia(parent);
                localidadRepo.save(loc);
            }
        });
        return localidadRepo.findAll().stream().collect(Collectors.toMap(Localidad::getNombre, l -> l));
    }

    private void loadCombustibles() throws IOException {
        InputStream is = new ClassPathResource("seed/combustible.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<CombustibleCsv> it = csvMapper.readerFor(CombustibleCsv.class)
                .with(schema).readValues(is);

        it.forEachRemaining(row -> {
            Combustible c = new Combustible();
            c.setNombre(row.getNombre());
            c.setNumeroOnu(row.getNumero_onu());
            c.setClaseRiesgo(row.getClase_riesgo());
            c.setDensidad(Double.parseDouble(row.getDensidad()));
            c.setTemperaturaReferencia(Double.parseDouble(row.getTemperatura_referencia()));
            combustibleRepo.save(c);
        });
    }

    private Map<String, Rol> loadRoles() throws IOException {
        InputStream is = new ClassPathResource("seed/roles.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Rol> it = csvMapper.readerFor(Rol.class)
                .with(schema).readValues(is);

        List<Rol> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            saved.add(rolRepo.save(row));
        });

        return saved.stream().collect(Collectors.toMap(Rol::getNombre, r -> r));
    }

    private Map<String, Permiso> loadPermisos() throws IOException {
        InputStream is = new ClassPathResource("seed/permisos.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Permiso> it = csvMapper.readerFor(Permiso.class)
                .with(schema).readValues(is);

        List<Permiso> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            saved.add(permisoRepo.save(row));
        });

        return saved.stream().collect(Collectors.toMap(Permiso::getCodigo, p -> p));
    }

    private void loadRolPermisos(Map<String, Rol> rolMap, Map<String, Permiso> permisoMap) throws IOException {
        InputStream is = new ClassPathResource("seed/rol_permisos.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Map<String, String>> it = csvMapper.readerFor(Map.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            Map<String, String> row = it.next();
            String rolNombre = row.get("rol_nombre");
            String permisoNombre = row.get("permiso_nombre");

            Rol rol = rolMap.get(rolNombre);
            Permiso permiso = permisoMap.get(permisoNombre);

            if (rol != null && permiso != null) {
                // 1. Add the permission to the Set inside the Rol
                if (rol.getPermisos() == null) {
                    rol.setPermisos(new HashSet<>());
                }
                rol.getPermisos().add(permiso);

                // 2. Save the Rol. JPA will automatically update the Rol_Permiso table.
                rolRepo.save(rol);
            } else {
                log.warn("Skipping link: Rol {} or Permiso {} not found", rolNombre, permisoNombre);
            }
        }
    }

    private Map<String, LugarOperativo> loadLugaresOperativos(Map<String, TipoLugarOperativo> tipoMap,
            Map<String, Localidad> localMap)
            throws IOException {
        InputStream is = new ClassPathResource("seed/lugares_operativos.csv").getInputStream();

        MappingIterator<LugarOperativoCsv> it = csvMapper.readerFor(LugarOperativoCsv.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(is);

        while (it.hasNext()) {
            LugarOperativoCsv row = it.next();
            LugarOperativo lugar = new LugarOperativo();

            // Basic Fields
            lugar.setNombre(row.getNombre());
            lugar.setDireccion(row.getDireccion());
            lugar.setLatitud(row.getLatitud());
            lugar.setLongitud(row.getLongitud());

            // Logic for 1/0 to Boolean
            lugar.setPuedeRecibir(row.getPuede_recibir() == 1);
            lugar.setPuedeDespachar(row.getPuede_despachar() == 1);
            lugar.setActivo(row.getActivo() == 1);

            // Linking Foreign Keys
            // We use the names from the CSV to "grab" the actual Objects from our maps
            lugar.setTipo(tipoMap.get(row.getTipo_nombre()));
            lugar.setLocalidad(localMap.get(row.getLocalidad_nombre()));

            // Hibernate handles fechaCreacion and fechaModificacion automatically here:
            lugarRepo.save(lugar);
        }
        return lugarRepo.findAll().stream().collect(Collectors.toMap(LugarOperativo::getNombre, l -> l));
    }

    private void loadUsuarios(Map<String, Rol> rolMap, Map<String, LugarOperativo> lugarMap) throws IOException {
        InputStream is = new ClassPathResource("seed/usuarios.csv").getInputStream();

        MappingIterator<UsuarioCsv> it = csvMapper.readerFor(UsuarioCsv.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(is);

        // BCrypt is the standard. If you don't have a PasswordEncoder bean yet,
        // you can use 'new BCryptPasswordEncoder()' for the seeder.
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        while (it.hasNext()) {
            UsuarioCsv row = it.next();
            Usuario user = new Usuario();

            user.setNombre(row.getNombre());
            user.setApellido(row.getApellido());
            user.setDni(row.getDni());
            user.setEmail(row.getEmail());
            user.setLegajo(row.getLegajo());
            user.setActivo(row.getActivo() == 1);

            // Security: Hash the password on the fly
            user.setPasswordHash(encoder.encode(row.getPassword_raw()));

            // Links
            user.setRol(rolMap.get(row.getRol_nombre()));
            user.setLugarOperativo(lugarMap.get(row.getLugar_nombre()));

            usuarioRepo.save(user);
        }
    }

}
