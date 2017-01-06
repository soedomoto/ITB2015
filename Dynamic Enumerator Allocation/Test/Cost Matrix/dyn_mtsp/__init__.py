class Enumerator:
    id = None
    latitude = None
    longitude = None
    visited_services = []
    time_elapsed = 0
    path_counter = -1

    def __init__(self):
        pass

    def __init__(self, id, lon, lat):
        self.id = id
        self.latitude = lat
        self.longitude = lon

    def set_depot(self, lon, lat):
        self.latitude = lat
        self.longitude = lon

    def get_depot(self):
        return (self.longitude, self. latitude)

    def visit(self, service_id):
        self.visited_services.append(service_id)

    def get_all_visited_ids(self):
        return self.visited_services

    def get_last_visited_id(self):
        if len(self.visited_services) == 0:
            return None
        return self.visited_services[len(self.visited_services) - 1]

    def set_time_elapsed(self, time):
        self.time_elapsed = time

    def get_time_elapsed(self):
        return self.time_elapsed

    def next_counter(self):
        self.path_counter += 1
        return self.path_counter


class Service:
    id = None
    latitude = None
    longitude = None

    def __init__(self):
        pass

    def __init__(self, id, lon, lat):
        self.id = id
        self.latitude = lat
        self.longitude = lon

    def set_coordinate(self, lon, lat):
        self.latitude = lat
        self.longitude = lon

    def get_coordinate(self):
        return (self.longitude, self.latitude)

    def __repr__(self):
        return self.id

class IListener:
    def on_start(self): pass
    def on_neighbors_found(self, enumerator, neighbors): pass
    def on_end(self): pass