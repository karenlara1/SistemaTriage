import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SolicitudService } from '../../solicitud.service';
import {
  Solicitud, Estado, TipoSolicitud, Prioridad,
  ESTADO_LABEL, TIPO_LABEL, PRIORIDAD_LABEL
} from '../../../core/models/solicitud.model';

@Component({
  selector: 'app-lista-solicitudes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lista-solicitudes.component.html',
  styleUrl: './lista-solicitudes.component.css'
})
export class ListaSolicitudesComponent implements OnInit {
  solicitudes: Solicitud[] = [];
  cargando = false;
  error = '';

  estados: Estado[] = ['REGISTRADA', 'CLASIFICADA', 'EN_ATENCION', 'ATENDIDA', 'CERRADA'];
  tipos: TipoSolicitud[] = ['REGISTRO_ASIGNATURA', 'HOMOLOGACION', 'CANCELACION', 'CUPO', 'CONSULTA'];
  prioridades: Prioridad[] = ['ALTA', 'MEDIA', 'BAJA'];

  filtros = {
    estado: '' as Estado | '',
    tipo: '' as TipoSolicitud | '',
    prioridad: '' as Prioridad | '',
    responsableId: ''
  };

  ESTADO_LABEL = ESTADO_LABEL;
  TIPO_LABEL = TIPO_LABEL;
  PRIORIDAD_LABEL = PRIORIDAD_LABEL;

  constructor(private solicitudService: SolicitudService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';

    this.solicitudService.listar(this.filtros).subscribe({
      next: data => {
        this.solicitudes = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No fue posible cargar las solicitudes.';
        this.cargando = false;
      }
    });
  }

  limpiar(): void {
    this.filtros = { estado: '', tipo: '', prioridad: '', responsableId: '' };
    this.cargar();
  }

  badgeEstado(estado: Estado): string {
    return `badge badge-${estado.toLowerCase()}`;
  }

  badgePrioridad(prioridad: Prioridad | null): string {
    return prioridad ? `badge badge-${prioridad.toLowerCase()}` : 'badge';
  }
}
