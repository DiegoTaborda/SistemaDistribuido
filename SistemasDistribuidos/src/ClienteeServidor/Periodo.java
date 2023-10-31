package ClienteeServidor;

import java.time.LocalTime;

public enum Periodo {
    MANHA(1, "manhã", LocalTime.parse("06:00"), LocalTime.parse("11:59")),
    TARDE(2, "tarde", LocalTime.parse("12:00"), LocalTime.parse("17:59")),
    NOITE(3, "noite", LocalTime.parse("18:00"), LocalTime.parse("23:59")),
    MADRUGADA(4, "madrugada", LocalTime.parse("00:00"), LocalTime.parse("05:59"));

    private final int codigo;
    private final String descricao;
    private final LocalTime inicio;
    private final LocalTime fim;

    Periodo(int codigo, String descricao, LocalTime inicio, LocalTime fim) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.inicio = inicio;
        this.fim = fim;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFim() {
        return fim;
    }
    
    
    public static boolean verificarPeriodo(int periodo, LocalTime horaIncidente) {
        Periodo periodoEnum = Periodo.getPeriodoByCodigo(periodo);

        if (periodoEnum != null) {
            LocalTime inicio = periodoEnum.getInicio();
            LocalTime fim = periodoEnum.getFim();

            if (inicio != null && fim != null) {
                return horaIncidente.isAfter(inicio) && horaIncidente.isBefore(fim);
            }
        }

        return false;
    }
    
    public static Periodo getPeriodoByCodigo(int codigo) {
        for (Periodo periodo : values()) {
            if (periodo.getCodigo() == codigo) {
                return periodo;
            }
        }
        return null;
    }

}

