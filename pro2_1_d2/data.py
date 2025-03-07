import json
import networkx as nx
import matplotlib.pyplot as plt
from duraklar import Durak

def load_data(file_path):
    with open(file_path, 'r') as f:
        return json.load(f)

def create_graph(duraklar, durak_type=None):
    G = nx.DiGraph()
    for durak in duraklar:
        if durak_type is None or durak.type == durak_type:
            G.add_node(durak.id, name=durak.name, lat=durak.lat, lon=durak.lon)
            for next_stop in durak.get_next_stops():
                G.add_edge(durak.id, next_stop['stopId'], mesafe=next_stop['mesafe'], sure=next_stop['sure'], ucret=next_stop['ucret'])
    return G

def draw_graph(G, path=None):
    pos = {node: (G.nodes[node]['lon'], G.nodes[node]['lat']) for node in G.nodes}
    node_color = 'red'
    edge_color = 'black'
    
    if path:
        node_color_map = [node_color if node in path else 'skyblue' for node in G.nodes]
        edge_color_map = [edge_color if (u, v) in zip(path, path[1:]) else 'black' for u, v in G.edges]
    else:
        node_color_map = 'skyblue'
        edge_color_map = 'black'
    
    nx.draw(G, pos, with_labels=True, node_size=500, node_color=node_color_map, edge_color=edge_color_map, font_size=10, font_weight='bold')
    if path:
        nx.draw_networkx_nodes(G, pos, nodelist=path, node_color='red')
        nx.draw_networkx_edges(G, pos, edgelist=list(zip(path, path[1:])), edge_color='orange', width=2)
    plt.show()

def create_all_graphs(file_path):
    data = load_data(file_path)
    duraklar = [Durak(**durak) for durak in data['duraklar']]

    all_stops_graph = create_graph(duraklar)
    bus_stops_graph = create_graph(duraklar, durak_type='bus')
    tram_stops_graph = create_graph(duraklar, durak_type='tram')

    return all_stops_graph, bus_stops_graph, tram_stops_graph