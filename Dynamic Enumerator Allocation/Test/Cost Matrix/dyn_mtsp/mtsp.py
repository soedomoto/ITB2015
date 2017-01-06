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

    def _get_best_first_services(self, service_ids, next_service_ids, duplicate_service_ids, enumerator_ids):
        # Get positions of duplicated ids
        duplicate_service_idxs = {}
        for d_id in duplicate_service_ids:
            duplicate_service_idxs.setdefault(d_id, [])
            for idx, s_id in enumerate(service_ids):
                if d_id == s_id: duplicate_service_idxs[d_id].append(idx)

        # Create base for alternative services --> by removing duplication
        base_alt_service_ids = copy.deepcopy(service_ids)
        for idx, s_id in enumerate(base_alt_service_ids):
            if s_id in duplicate_service_ids: base_alt_service_ids[idx] = None

        all_comb_ids = list(itertools.product(*duplicate_service_idxs.values()))

        # List all alternatives services combinations
        alts_service_ids = []
        for comb_ids in all_comb_ids:
            alt_service_ids = copy.deepcopy(base_alt_service_ids)
            to_be_changed_idxs = copy.deepcopy(duplicate_service_idxs)

            for i, s_idx in enumerate(comb_ids):
                alt_service_ids[s_idx] = duplicate_service_idxs.keys()[i]
                to_be_changed_idxs[alt_service_ids[s_idx]].remove(s_idx)

            for c_idxs in to_be_changed_idxs.values():
                for c_idx in c_idxs:
                    for i in range(0, len(next_service_ids[c_idx])):
                        if next_service_ids[c_idx][i] not in alt_service_ids:
                            alt_service_ids[c_idx] = next_service_ids[c_idx][i]
                            break

            alts_service_ids.append(alt_service_ids)

        # Compare each alternative time
        least_time = None
        best_alt_service_ids = None
        for alt_service_ids in alts_service_ids:
            time = 0.0
            for i, s_id in enumerate(alt_service_ids):
                if s_id: time += self.time_weight[enumerator_ids[i]][s_id]

            if (not least_time) or (time < least_time):
                least_time = time
                best_alt_service_ids = alt_service_ids

        return best_alt_service_ids

    def _get_first_services(self, enumerator_services):
        # Detect duplicate services
        first_service_ids = []
        next_service_ids = []
        first_service_enumerator_ids = []
        for enumerator, services in enumerator_services.items():
            first_service_ids.append(services[0].id)
            next_service_ids.append([service.id for service in services[1:] if len(services) > 1])
            first_service_enumerator_ids.append(enumerator.id)

        # Find duplicate first services
        duplicate_first_service_ids = list(set([x for x in first_service_ids if first_service_ids.count(x) > 1]))

        assigned_first_service_ids = {}
        if len(duplicate_first_service_ids) > 0:
            first_service_ids = self._get_best_first_services(first_service_ids, next_service_ids,
                                                              duplicate_first_service_ids, first_service_enumerator_ids)

        for idx, s_id in enumerate(first_service_ids):
            assigned_first_service_ids[first_service_enumerator_ids[idx]] = s_id

        return assigned_first_service_ids

    def _next(self, listener):
        # Dont process if no service left
        if len(self.services) == 0: return []

        # Create nearest neighbor model
        nbrs = NearestNeighbors(n_neighbors=1, algorithm='auto', metric=self._knn_weight)
        nbrs.fit([[id, ] for id, service in self.services.items()])

        # Find neighbors
        enumerator_services = {}
        for e_id, enumerator in self.enumerators.items():
            services = nbrs.kneighbors([[e_id, ]], n_neighbors=len(self.services), return_distance=False)
            services = [self.services.values()[n_id] for n_id in services[0]]
            listener.on_neighbors_found(enumerator, services)
            enumerator_services[enumerator] = services

        return self._get_first_services(enumerator_services)

    def solve(self, listener):
        listener.on_start()

        self.visited = []
        solutions = {e_id: [] for e_id in self.enumerators.keys()}
        while True:
            assigned_service_ids = self._next(listener)
            if len(assigned_service_ids) == 0: break

            for e_id, s_id in assigned_service_ids.items():
                if s_id:
                    solutions[e_id].append(self.services.pop(s_id, None))
                    self.visited.append(s_id)

        return solutions


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


    class Listener(IListener):
        def on_start(self):
            pass

        def on_neighbors_found(self, enumerator, neighbors):
            # print '{} : {}'.format(enumerator.id, [neighbor.id for neighbor in neighbors])
            pass

        def on_end(self):
            pass


    solution = mtsp.solve(Listener())
    for e_id, services in solution.items():
        time = 0
        last_service = None
        service_ids = []
        for service in services:
            service_ids.append(service.id)
            if last_service:
                time += mtsp.time_weight[last_service.id][service.id]
            last_service = service

        print '{} ({}) = {}'.format(e_id, time, ' -> '.join(service_ids))
