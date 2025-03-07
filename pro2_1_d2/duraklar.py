class Durak:
    def __init__(self, id, name, type, lat=None, lon=None, sonDurak=False, nextStops=None, transfer=None):
        self.id = id
        self.name = name
        self.type = type
        self.lat = lat
        self.lon = lon
        self.sonDurak = sonDurak
        self.nextStops = nextStops if nextStops else []
        self.transfer = transfer

    def __str__(self):
        return f"{self.name} ({self.type})"

    def get_next_stops(self):
        return self.nextStops

    def get_transfer(self):
        return self.transfer