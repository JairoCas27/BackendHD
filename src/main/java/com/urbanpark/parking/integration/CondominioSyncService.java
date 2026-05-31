package main.java.com.urbanpark.parking.integration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CondominioSyncService {
    
    private final CondominioApiClient apiClient;
    private final UsuarioRepository usuarioRepository;
    
    public CondominioSyncService(CondominioApiClient apiClient, UsuarioRepository usuarioRepository) {
        this.apiClient = apiClient;
        this.usuarioRepository = usuarioRepository;
    }
    
    // Sincroniza usuarios cada 30 minutos
    @Scheduled(fixedRate = 1800000)
    public void sincronizarUsuarios() {
        List<UsuarioExterno> usuarios = apiClient.obtenerUsuariosActivos();
        for (UsuarioExterno u : usuarios) {
            usuarioRepository.saveOrUpdate(u);
        }
    }
    
    // Sincroniza espacios de parqueo disponibles
    public void sincronizarEspacios(String condominioId) {
        List<EspacioParqueo> espacios = apiClient.obtenerEspacios(condominioId);
        // guardar en BD local
    }
}
