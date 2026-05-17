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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.blackmesaresearch.hytrac.dto.csv.AcopladoCsv;
import com.blackmesaresearch.hytrac.dto.csv.CombustibleCsv;
import com.blackmesaresearch.hytrac.dto.csv.EmpresaTercerizadaCsv;
import com.blackmesaresearch.hytrac.dto.csv.LocalidadCsv;
import com.blackmesaresearch.hytrac.dto.csv.LugarOperativoCsv;
import com.blackmesaresearch.hytrac.dto.csv.OrdenCargaCsv;
import com.blackmesaresearch.hytrac.dto.csv.TransportistaCsv;
import com.blackmesaresearch.hytrac.dto.csv.VehiculoCsv;
import com.blackmesaresearch.hytrac.dto.csv.ProvinciaCsv;
import com.blackmesaresearch.hytrac.dto.csv.UsuarioCsv;
import com.blackmesaresearch.hytrac.model.core.Acoplado;
import com.blackmesaresearch.hytrac.model.core.EmpresaTercerizada;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.core.Usuario;
import com.blackmesaresearch.hytrac.model.core.Vehiculo;
import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;
import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;
import com.blackmesaresearch.hytrac.model.lookup.Permiso;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.lookup.TipoLugarOperativo;
import com.blackmesaresearch.hytrac.model.lookup.TipoVinculo;
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
    private final EmpresaTercerizadaRepository empresaTercerizadaRepo;
    private final TransportistaRepository transportistaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final AcopladoRepository acopladoRepo;
    private final TipoVinculoRepository tipoVinculoRepo;
    private final EstadoVehiculoRepository estadoVehiculoRepo;
    private final OrdenCargaRepository ordenCargaRepo;
    private final EstadoOrdenCargaRepository estadoOrdenCargaRepo;
    private final CsvMapper csvMapper;

    public DataSeeder(
            ProvinciaRepository provinciaRepo,
            LocalidadRepository localidadRepo,
            CombustibleRepository combustibleRepo,
            RolRepository rolRepo,
            PermisoRepository permisoRepo,
            LugarOperativoRepository lugarRepo,
            TipoLugarOperativoRepository tipoLugarOperativoRepo,
            UsuarioRepository usuarioRepo,
            EmpresaTercerizadaRepository empresaTercerizadaRepo,
            TransportistaRepository transportistaRepo,
            VehiculoRepository vehiculoRepo,
            AcopladoRepository acopladoRepo,
            TipoVinculoRepository tipoVinculoRepo,
            EstadoVehiculoRepository estadoVehiculoRepo,
            OrdenCargaRepository ordenCargaRepo,
            EstadoOrdenCargaRepository estadoOrdenCargaRepo) {
        this.provinciaRepo = provinciaRepo;
        this.localidadRepo = localidadRepo;
        this.combustibleRepo = combustibleRepo;
        this.rolRepo = rolRepo;
        this.permisoRepo = permisoRepo;
        this.lugarRepo = lugarRepo;
        this.tipoLugarOperativoRepo = tipoLugarOperativoRepo;
        this.usuarioRepo = usuarioRepo;
        this.empresaTercerizadaRepo = empresaTercerizadaRepo;
        this.transportistaRepo = transportistaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.acopladoRepo = acopladoRepo;
        this.tipoVinculoRepo = tipoVinculoRepo;
        this.estadoVehiculoRepo = estadoVehiculoRepo;
        this.ordenCargaRepo = ordenCargaRepo;
        this.estadoOrdenCargaRepo = estadoOrdenCargaRepo;
        this.csvMapper = new CsvMapper();
        this.csvMapper.registerModule(new JavaTimeModule());
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

        // 3. Load Empresas Tercerizadas
        loadEmpresasTercerizadas(localidadMap);

        // 4. Load Combustibles
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

        // 9. Load Transportistas
        loadTransportistas();

        // 10. Load Vehiculos
        loadVehiculos();

        // 11. Load Acoplados
        loadAcoplados();

        // 12. Load Ordenes de Carga
        loadOrdenesCarga();

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

    private void loadEmpresasTercerizadas(Map<String, Localidad> localidadMap) throws IOException {
        InputStream is = new ClassPathResource("seed/empresas_tercerizadas.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<EmpresaTercerizadaCsv> it = csvMapper.readerFor(EmpresaTercerizadaCsv.class)
                .with(schema).readValues(is);

        it.forEachRemaining(row -> {
            EmpresaTercerizada empresa = new EmpresaTercerizada();
            empresa.setNombreFantasia(row.getNombre_fantasia());
            empresa.setRazonSocial(row.getRazon_social());
            empresa.setCuit(row.getCuit());
            empresa.setDireccion(row.getDireccion());
            empresa.setTelefono(row.getTelefono());
            empresa.setActivo(row.getActivo() == 1);
            empresa.setLocalidad(localidadMap.get(row.getLocalidad_nombre()));
            empresaTercerizadaRepo.save(empresa);
        });
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

    private void loadTransportistas() throws IOException {
        InputStream is = new ClassPathResource("seed/transportistas.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        Map<String, TipoVinculo> tipoVinculoMap = tipoVinculoRepo.findAll().stream()
                .collect(Collectors.toMap(TipoVinculo::getNombre, t -> t));

        Map<String, EmpresaTercerizada> empresaMap = new java.util.HashMap<>();
        empresaTercerizadaRepo.findAll().forEach(e -> {
            if (e.getNombreFantasia() != null) {
                empresaMap.put(e.getNombreFantasia(), e);
            }
            if (e.getRazonSocial() != null) {
                empresaMap.putIfAbsent(e.getRazonSocial(), e);
            }
        });

        MappingIterator<TransportistaCsv> it = csvMapper.readerFor(TransportistaCsv.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            TransportistaCsv row = it.next();
            Usuario usuario = null;
            if (row.getUsuario_email() != null && !row.getUsuario_email().isBlank()) {
                usuario = usuarioRepo.findByEmail(row.getUsuario_email()).orElse(null);
            }
            if (usuario == null && row.getUsuario_legajo() != null && !row.getUsuario_legajo().isBlank()) {
                usuario = usuarioRepo.findByLegajo(row.getUsuario_legajo()).orElse(null);
            }

            TipoVinculo tipoVinculo = tipoVinculoMap.get(row.getTipo_vinculo_nombre());
            EmpresaTercerizada empresa = empresaMap.get(row.getEmpresa_nombre());

            if (usuario == null || tipoVinculo == null || empresa == null) {
                log.warn("Skipping transportista because user/type/empresa not found: {} / {} / {}",
                        row.getUsuario_email() != null ? row.getUsuario_email() : row.getUsuario_legajo(),
                        row.getTipo_vinculo_nombre(),
                        row.getEmpresa_nombre());
                continue;
            }

            Transportista transportista = new Transportista();
            transportista.setUsuario(usuario);
            transportista.setTipoVinculo(tipoVinculo);
            transportista.setCuit(row.getCuit());
            transportista.setEmpresa(empresa);
            transportista.setActivo(row.getActivo() == 1);

            transportistaRepo.save(transportista);
        }
    }

    private void loadVehiculos() throws IOException {
        InputStream is = new ClassPathResource("seed/vehiculo.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        Map<String, EmpresaTercerizada> empresaMap = new java.util.HashMap<>();
        empresaTercerizadaRepo.findAll().forEach(e -> {
            if (e.getNombreFantasia() != null) {
                empresaMap.put(e.getNombreFantasia(), e);
            }
            if (e.getRazonSocial() != null) {
                empresaMap.putIfAbsent(e.getRazonSocial(), e);
            }
        });

        Map<String, EstadoVehiculo> estadoMap = estadoVehiculoRepo.findAll().stream()
                .collect(Collectors.toMap(EstadoVehiculo::getNombre, e -> e));

        MappingIterator<VehiculoCsv> it = csvMapper.readerFor(VehiculoCsv.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            VehiculoCsv row = it.next();
            EmpresaTercerizada empresa = empresaMap.get(row.getEmpresa_nombre());
            EstadoVehiculo estado = estadoMap.get(row.getEstado_nombre());

            if (empresa == null || estado == null) {
                log.warn("Skipping vehiculo because empresa or estado not found: {} / {}",
                        row.getEmpresa_nombre(), row.getEstado_nombre());
                continue;
            }

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPatente(row.getPatente());
            vehiculo.setEmpresa(empresa);
            vehiculo.setPeso_maximo_admitido(row.getPeso_maximo_admitido());
            vehiculo.setMarca(row.getMarca());
            vehiculo.setModelo(row.getModelo());
            vehiculo.setEstado(estado);

            vehiculoRepo.save(vehiculo);
        }
    }

    private void loadAcoplados() throws IOException {
        InputStream is = new ClassPathResource("seed/acoplado.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        Map<String, EmpresaTercerizada> empresaMap = new java.util.HashMap<>();
        empresaTercerizadaRepo.findAll().forEach(e -> {
            if (e.getNombreFantasia() != null) {
                empresaMap.put(e.getNombreFantasia(), e);
            }
            if (e.getRazonSocial() != null) {
                empresaMap.putIfAbsent(e.getRazonSocial(), e);
            }
        });

        Map<String, EstadoVehiculo> estadoMap = estadoVehiculoRepo.findAll().stream()
                .collect(Collectors.toMap(EstadoVehiculo::getNombre, e -> e));

        MappingIterator<AcopladoCsv> it = csvMapper.readerFor(AcopladoCsv.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            AcopladoCsv row = it.next();
            EmpresaTercerizada empresa = empresaMap.get(row.getEmpresa_nombre());
            EstadoVehiculo estado = estadoMap.get(row.getEstado_nombre());

            if (empresa == null || estado == null) {
                log.warn("Skipping acoplado because empresa or estado not found: {} / {}",
                        row.getEmpresa_nombre(), row.getEstado_nombre());
                continue;
            }

            Acoplado acoplado = new Acoplado();
            acoplado.setPatente(row.getPatente());
            acoplado.setCapacidadMaximaLitros(row.getCapacidad_maxima_litros());
            acoplado.setEmpresa(empresa);
            acoplado.setEstado(estado);

            acopladoRepo.save(acoplado);
        }
    }

    private void loadOrdenesCarga() throws IOException {
        InputStream is = new ClassPathResource("seed/orden_carga.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        Map<String, Vehiculo> vehiculoMap = vehiculoRepo.findAll().stream()
                .collect(Collectors.toMap(Vehiculo::getPatente, v -> v));
        Map<String, Acoplado> acopladoMap = acopladoRepo.findAll().stream()
                .collect(Collectors.toMap(Acoplado::getPatente, a -> a));
        Map<String, Transportista> transportistaMap = transportistaRepo.findAll().stream()
                .collect(Collectors.toMap(t -> t.getUsuario().getLegajo(), t -> t));
        Map<String, LugarOperativo> lugarMap = lugarRepo.findAll().stream()
                .collect(Collectors.toMap(LugarOperativo::getNombre, l -> l));
        Map<String, Usuario> operadorMap = usuarioRepo.findAll().stream()
                .collect(Collectors.toMap(Usuario::getLegajo, u -> u));
        Map<String, Combustible> combustibleMap = combustibleRepo.findAll().stream()
                .collect(Collectors.toMap(Combustible::getNombre, c -> c));
        Map<String, EstadoOrdenCarga> estadoMap = estadoOrdenCargaRepo.findAll().stream()
                .collect(Collectors.toMap(EstadoOrdenCarga::getNombre, e -> e));

        MappingIterator<OrdenCargaCsv> it = csvMapper.readerFor(OrdenCargaCsv.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            OrdenCargaCsv row = it.next();

            Vehiculo camion = vehiculoMap.get(row.getCamionPatente());
            Acoplado acoplado = acopladoMap.get(row.getAcopladoPatente());
            Transportista transportista = transportistaMap.get(row.getTransportistaLegajo());
            LugarOperativo plantaDespacho = lugarMap.get(row.getPlantaDespachoNombre());
            LugarOperativo estacionDestino = lugarMap.get(row.getEstacionDestinoNombre());
            Usuario operador = operadorMap.get(row.getOperadorLegajo());
            Combustible combustible = combustibleMap.get(row.getCombustibleNombre());
            EstadoOrdenCarga estado = estadoMap.get(row.getEstadoNombre());

            if (camion == null || acoplado == null || transportista == null || plantaDespacho == null
                    || estacionDestino == null || operador == null || combustible == null || estado == null) {
                log.warn("Skipping orden_carga because referenced record not found: {} [camion={}, acoplado={}, transportista={}, plantaDespacho={}, estacionDestino={}, operador={}, combustible={}, estado={}]",
                        row.getNumeroRemito(), row.getCamionPatente(), row.getAcopladoPatente(), row.getTransportistaLegajo(), row.getPlantaDespachoNombre(), row.getEstacionDestinoNombre(), row.getOperadorLegajo(), row.getCombustibleNombre(), row.getEstadoNombre());
                continue;
            }

            OrdenCarga orden = new OrdenCarga();
            orden.setNumeroRemito(row.getNumeroRemito());
            orden.setCot(row.getCot());
            orden.setCamion(camion);
            orden.setAcoplado(acoplado);
            orden.setTransportista(transportista);
            orden.setPlantaDespacho(plantaDespacho);
            orden.setEstacionDestino(estacionDestino);
            orden.setOperador(operador);
            orden.setCombustible(combustible);
            orden.setEstadoOrdenCarga(estado);
            orden.setFechaCreacion(row.getFechaCreacion());
            orden.setFechaSalidaPlanta(row.getFechaSalidaPlanta());
            orden.setFechaEntregaEstimada(row.getFechaEntregaEstimada());
            orden.setTemperaturaCarga(row.getTemperaturaCarga());
            orden.setDensidadCarga(row.getDensidadCarga());
            orden.setLitrosCargados(row.getLitrosCargados());
            orden.setLitrosEntregados(row.getLitrosEntregados());
            orden.setFieAdjunta(row.getFieAdjunta());
            orden.setObservaciones(row.getObservaciones());
            orden.setConfirmado(row.getConfirmado());

            ordenCargaRepo.save(orden);
        }
    }
}
