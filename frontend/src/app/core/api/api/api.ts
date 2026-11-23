export * from './admissions.service';
import { AdmissionsService } from './admissions.service';
export * from './pathologies.service';
import { PathologiesService } from './pathologies.service';
export * from './patients.service';
import { PatientsService } from './patients.service';
export * from './sIOResources.service';
import { SIOResourcesService } from './sIOResources.service';
export const APIS = [AdmissionsService, PathologiesService, PatientsService, SIOResourcesService];
