package co.edu.ucc.pasto3d.ia;

/** DECORATOR: mide cuánto tarda cada respuesta y lo registra en las métricas (SINGLETON). */
public class ProveedorIAMedido extends ProveedorIADecorador {

    public ProveedorIAMedido(ProveedorIA envuelto) { super(envuelto); }

    @Override
    public String responder(String pregunta, ContextoCampus contexto, String indicaciones) {
        long inicio = System.nanoTime();
        String r = envuelto.responder(pregunta, contexto, indicaciones);
        MetricasAsistente.instancia().registrar(nombre(), (System.nanoTime() - inicio) / 1_000_000);
        return r;
    }
}
