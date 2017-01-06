import copy
import itertools
import threading

import time
import unicodecsv as csv
from sklearn.neighbors import NearestNeighbors

from dyn_mtsp import Enumerator, Service, IListener


class AMTSP:
    enumerators = {}
    services = {}
    time_weight = {}
    visited_locations = []

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

    def _enumerator_worker(self, enumerator, service_ids):
        while True:
            c = enumerator.next_counter()
            if c >= len(service_ids): break

            if service_ids[c] not in self.visited_locations:
                print enumerator.id, 'is visiting', service_ids[c]

                self.visited_locations.append(service_ids[c])

                last_id = enumerator.get_last_visited_id()
                enumerator.visit(service_ids[c])

                if last_id:
                    enumerator.set_time_elapsed(self.time_weight[last_id][service_ids[c]])
                    time.sleep(self.time_weight[last_id][service_ids[c]]/2000)

    def solve(self, listener):
        # Create nearest neighbor model
        nbrs = NearestNeighbors(n_neighbors=1, algorithm='auto', metric=self._knn_weight)
        nbrs.fit([[id, ] for id, service in self.services.items()])

        # Find neighbors
        enumerator_service_path = {}
        for e_id, enumerator in self.enumerators.items():
            services = nbrs.kneighbors([[e_id, ]], n_neighbors=len(self.services), return_distance=False)
            services = [self.services.keys()[n_id] for n_id in services[0]]
            enumerator_service_path[enumerator.id] = services

        # Analyze service path
        threads = []
        for e_id, s_ids in enumerator_service_path.items():
            t = threading.Thread(target=self._enumerator_worker, args=(self.enumerators[e_id], s_ids,))
            threads.append(t)

        for t in threads: t.start()
        for t in threads: t.join()

        for e in self.enumerators.values():
            print e.id, e.get_time_elapsed()
            print e.id, ' -> '.join(e.get_all_visited_ids())


if __name__ == '__main__':
    mtsp = AMTSP()

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
