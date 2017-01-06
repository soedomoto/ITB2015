import copy

import itertools
import unicodecsv as csv
from sklearn.neighbors import NearestNeighbors
from dyn_mtsp import Enumerator, Service, IListener


class MTSP:
    enumerators = {}
    services = {}
    time_weight = {}

    def __init__(self):
        pass

    def add_enumerator(self, enumerator):
        self.enumerators[enumerator.id] = enumerator

    def add_service(self, service):
        self.services[service.id] = service

    def add_time_weight(self, service_a, service_b, seconds, is_symmetric=False):
        self.time_weight.setdefault(service_a, {})
        self.time_weight[service_a][service_b] = seconds

        if is_symmetric:
            self.time_weight.setdefault(service_b, {})
            self.time_weight[service_b][service_a] = seconds

    def _knn_weight(self, id_a, id_b):
        id_a = str(int(id_a))
        id_b = str(int(id_b))

        if (id_a in self.time_weight and id_b in self.time_weight[id_a]):
            return self.time_weight[id_a][id_b]
        return 0.0

    def _clusters_contains(self, clusters, s_id):
        for cluster in clusters:
            if s_id in cluster:
                return True
        return False

    def solve(self, listener):
        # Create nearest neighbor model
        nbrs = NearestNeighbors(n_neighbors=1, algorithm='auto', metric=self._knn_weight)
        nbrs.fit([[id, ] for id, service in self.services.items()])

        # Find neighbors
        enumerator_service_path = {}
        for e_id, enumerator in self.enumerators.items():
            service_ids = nbrs.kneighbors([[e_id, ]], n_neighbors=len(self.services), return_distance=False)
            service_ids = [self.services.keys()[n_id] for n_id in service_ids[0]]
            enumerator_service_path[enumerator.id] = service_ids

        # Create clusters
        e_ids = enumerator_service_path.keys()
        services_ids = enumerator_service_path.values()
        t_services_ids = zip(*services_ids)

        clusters = [[] for e in e_ids]
        for i, t_service_ids in enumerate(t_services_ids):
            t_service_ids = list(t_service_ids)
            duplicate_s_ids = list(set([x for x in t_service_ids if t_service_ids.count(x) > 1]))

            if len(duplicate_s_ids) > 0:
                for duplicate_s_id in duplicate_s_ids:
                    duplicate_s_idxs = [j for j, s in enumerate(t_service_ids) if s == duplicate_s_id]

                    # To keep t_service_ids size, replace duplicated indices with None
                    for idx in duplicate_s_idxs:
                        t_service_ids[idx] = None

                    # Compare distance to each duplicated enumerators
                    min_distance = float("inf")
                    min_idx = None
                    for idx in duplicate_s_idxs:
                        if self.time_weight[e_ids[idx]][duplicate_s_id] < min_distance:
                            min_idx = idx
                            min_distance = self.time_weight[e_ids[idx]][duplicate_s_id]

                    # Restore id
                    t_service_ids[min_idx] = duplicate_s_id

            for idx, id in enumerate(t_service_ids):
                if id and not self._clusters_contains(clusters, id):
                    clusters[idx].append(id)

        # Process each cluster
        for cluster in clusters:
            print len(list(itertools.permutations(cluster, len(cluster))))


if __name__ == '__main__':
    mtsp = MTSP()

    table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/'
                            'Dynamic Enumerator Allocation/Test/Data/enumerators.csv'))
    for id, lat, lon in list(table)[1:]:
        mtsp.add_enumerator(Enumerator(id, float(lon), float(lat)))

    table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/'
                            'Dynamic Enumerator Allocation/Test/Data/coord_nagari_2014.csv'))
    for id, lat, lon in list(table)[1:]:
        mtsp.add_service(Service(id, float(lon), float(lat)))

    table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/'
                            'Dynamic Enumerator Allocation/Test/Data/cost_matrix_nagari_2014.csv'))
    for id_a, id_b, distance, duration, status in list(table)[1:]:
        mtsp.add_time_weight(id_a, id_b, float(duration))

    solution = mtsp.solve(IListener())
