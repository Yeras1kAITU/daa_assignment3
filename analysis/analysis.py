import json
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import os

class MSTAnalyzer:
    def __init__(self, results_dir="../results"):
        self.results_dir = results_dir
        self.setup_plot_style()

    def setup_plot_style(self):
        """Configure plot style"""
        plt.rcParams['figure.figsize'] = [12, 8]
        plt.rcParams['font.size'] = 12
        plt.rcParams['font.family'] = 'DejaVu Sans'
        sns.set_palette("husl")

    def load_all_results(self):
        """Load all results from JSON files"""
        all_data = []

        for filename in os.listdir(self.results_dir):
            if filename.endswith('_results.json'):
                filepath = os.path.join(self.results_dir, filename)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        data = json.load(f)

                    for result in data['results']:
                        vertices = result['input_stats']['vertices']
                        edges = result['input_stats']['edges']
                        max_edges = vertices * (vertices - 1) / 2
                        density = edges / max_edges if max_edges > 0 else 0

                        graph_data = {
                            'graph_file': filename,
                            'graph_id': result['graph_id'],
                            'vertices': vertices,
                            'edges': edges,
                            'prim_cost': result['prim']['total_cost'],
                            'kruskal_cost': result['kruskal']['total_cost'],
                            'prim_time': result['prim']['execution_time_ms'],
                            'kruskal_time': result['kruskal']['execution_time_ms'],
                            'prim_operations': result['prim']['operations_count'],
                            'kruskal_operations': result['kruskal']['operations_count'],
                            'density': density
                        }
                        all_data.append(graph_data)

                except Exception as e:
                    print(f"Error loading {filename}: {e}")

        return pd.DataFrame(all_data)

    def plot_execution_time_analysis(self, df):
        """Execution time analysis"""
        fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('MST Algorithms Execution Time Analysis', fontsize=16, fontweight='bold')

        # 1. Time vs Number of vertices
        ax1.scatter(df['vertices'], df['prim_time'], alpha=0.7, label='Prim', s=60, color='blue')
        ax1.scatter(df['vertices'], df['kruskal_time'], alpha=0.7, label='Kruskal', s=60, color='red')

        # Add trend lines
        if len(df) > 1:
            # Prim trend line
            z_prim = np.polyfit(df['vertices'], df['prim_time'], 1)
            p_prim = np.poly1d(z_prim)
            x_range = np.linspace(df['vertices'].min(), df['vertices'].max(), 100)
            ax1.plot(x_range, p_prim(x_range), 'b-', alpha=0.5, linewidth=2,
                     label=f'Prim trend (slope: {z_prim[0]:.4f})')

            # Kruskal trend line
            z_kruskal = np.polyfit(df['vertices'], df['kruskal_time'], 1)
            p_kruskal = np.poly1d(z_kruskal)
            ax1.plot(x_range, p_kruskal(x_range), 'r-', alpha=0.5, linewidth=2,
                     label=f'Kruskal trend (slope: {z_kruskal[0]:.4f})')

        ax1.set_xlabel('Number of vertices (n)')
        ax1.set_ylabel('Execution time (ms)')
        ax1.set_title('Execution Time vs Number of Vertices')
        ax1.legend()
        ax1.grid(True, alpha=0.3)

        # 2. Time vs Number of edges
        ax2.scatter(df['edges'], df['prim_time'], alpha=0.7, label='Prim', s=60, color='blue')
        ax2.scatter(df['edges'], df['kruskal_time'], alpha=0.7, label='Kruskal', s=60, color='red')

        # Add trend lines for edges
        if len(df) > 1:
            # Prim trend line for edges
            z_prim_edges = np.polyfit(df['edges'], df['prim_time'], 1)
            p_prim_edges = np.poly1d(z_prim_edges)
            x_range_edges = np.linspace(df['edges'].min(), df['edges'].max(), 100)
            ax2.plot(x_range_edges, p_prim_edges(x_range_edges), 'b-', alpha=0.5, linewidth=2,
                     label=f'Prim trend (slope: {z_prim_edges[0]:.6f})')

            # Kruskal trend line for edges
            z_kruskal_edges = np.polyfit(df['edges'], df['kruskal_time'], 1)
            p_kruskal_edges = np.poly1d(z_kruskal_edges)
            ax2.plot(x_range_edges, p_kruskal_edges(x_range_edges), 'r-', alpha=0.5, linewidth=2,
                     label=f'Kruskal trend (slope: {z_kruskal_edges[0]:.6f})')

        ax2.set_xlabel('Number of edges (m)')
        ax2.set_ylabel('Execution time (ms)')
        ax2.set_title('Execution Time vs Number of Edges')
        ax2.legend()
        ax2.grid(True, alpha=0.3)

        # 3. Time ratio
        time_ratio = df['prim_time'] / df['kruskal_time']
        colors = ['green' if ratio <= 1 else 'orange' for ratio in time_ratio]

        ax3.scatter(df['vertices'], time_ratio, alpha=0.7, s=60, c=colors)
        ax3.axhline(y=1, color='red', linestyle='--', alpha=0.8, linewidth=2,
                    label='Equal performance')

        # Add average ratio line
        avg_ratio = time_ratio.mean()
        ax3.axhline(y=avg_ratio, color='purple', linestyle='-', alpha=0.6, linewidth=1,
                    label=f'Average ratio: {avg_ratio:.3f}')

        ax3.set_xlabel('Number of vertices (n)')
        ax3.set_ylabel('Time ratio Prim/Kruskal')
        ax3.set_title('Execution Time Ratio\n(green: Prim faster, orange: Kruskal faster)')
        ax3.legend()
        ax3.grid(True, alpha=0.3)

        # 4. Time vs Graph density
        ax4.scatter(df['density'], df['prim_time'], alpha=0.7, label='Prim', s=60, color='blue')
        ax4.scatter(df['density'], df['kruskal_time'], alpha=0.7, label='Kruskal', s=60, color='red')
        ax4.set_xlabel('Graph density')
        ax4.set_ylabel('Execution time (ms)')
        ax4.set_title('Execution Time vs Graph Density')
        ax4.legend()
        ax4.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig('../results/execution_time_analysis.png', dpi=300, bbox_inches='tight')
        plt.show()

    def plot_operations_complexity(self, df):
        """Operations complexity analysis"""
        fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('MST Algorithms Computational Complexity Analysis', fontsize=16, fontweight='bold')

        # 1. Operations vs Vertices
        ax1.scatter(df['vertices'], df['prim_operations'], alpha=0.7, label='Prim', s=60, color='blue')
        ax1.scatter(df['vertices'], df['kruskal_operations'], alpha=0.7, label='Kruskal', s=60, color='red')

        # Add trend lines
        if len(df) > 1:
            # Prim trend line
            z_prim = np.polyfit(df['vertices'], df['prim_operations'], 1)
            p_prim = np.poly1d(z_prim)
            x_range = np.linspace(df['vertices'].min(), df['vertices'].max(), 100)
            ax1.plot(x_range, p_prim(x_range), 'b-', alpha=0.5, linewidth=2,
                     label=f'Prim trend (slope: {z_prim[0]:.1f})')

            # Kruskal trend line
            z_kruskal = np.polyfit(df['vertices'], df['kruskal_operations'], 1)
            p_kruskal = np.poly1d(z_kruskal)
            ax1.plot(x_range, p_kruskal(x_range), 'r-', alpha=0.5, linewidth=2,
                     label=f'Kruskal trend (slope: {z_kruskal[0]:.1f})')

        ax1.set_xlabel('Number of vertices (n)')
        ax1.set_ylabel('Number of operations')
        ax1.set_title('Operations vs Number of Vertices')
        ax1.legend()
        ax1.grid(True, alpha=0.3)

        # 2. Operations vs Edges
        ax2.scatter(df['edges'], df['prim_operations'], alpha=0.7, label='Prim', s=60, color='blue')
        ax2.scatter(df['edges'], df['kruskal_operations'], alpha=0.7, label='Kruskal', s=60, color='red')

        # Add trend lines for edges
        if len(df) > 1:
            # Prim trend line for edges
            z_prim_edges = np.polyfit(df['edges'], df['prim_operations'], 1)
            p_prim_edges = np.poly1d(z_prim_edges)
            x_range_edges = np.linspace(df['edges'].min(), df['edges'].max(), 100)
            ax2.plot(x_range_edges, p_prim_edges(x_range_edges), 'b-', alpha=0.5, linewidth=2,
                     label=f'Prim trend (slope: {z_prim_edges[0]:.3f})')

            # Kruskal trend line for edges
            z_kruskal_edges = np.polyfit(df['edges'], df['kruskal_operations'], 1)
            p_kruskal_edges = np.poly1d(z_kruskal_edges)
            ax2.plot(x_range_edges, p_kruskal_edges(x_range_edges), 'r-', alpha=0.5, linewidth=2,
                     label=f'Kruskal trend (slope: {z_kruskal_edges[0]:.3f})')

        ax2.set_xlabel('Number of edges (m)')
        ax2.set_ylabel('Number of operations')
        ax2.set_title('Operations vs Number of Edges')
        ax2.legend()
        ax2.grid(True, alpha=0.3)

        # 3. Operations ratio
        operations_ratio = df['prim_operations'] / df['kruskal_operations']
        colors_ops = ['green' if ratio <= 1 else 'orange' for ratio in operations_ratio]

        ax3.scatter(df['vertices'], operations_ratio, alpha=0.7, s=60, c=colors_ops)
        ax3.axhline(y=1, color='red', linestyle='--', alpha=0.8, linewidth=2,
                    label='Equal operations')

        # Add average ratio line
        avg_ops_ratio = operations_ratio.mean()
        ax3.axhline(y=avg_ops_ratio, color='purple', linestyle='-', alpha=0.6, linewidth=1,
                    label=f'Average ratio: {avg_ops_ratio:.3f}')

        ax3.set_xlabel('Number of vertices (n)')
        ax3.set_ylabel('Operations ratio Prim/Kruskal')
        ax3.set_title('Operations Ratio\n(green: Prim fewer ops, orange: Kruskal fewer ops)')
        ax3.legend()
        ax3.grid(True, alpha=0.3)

        # 4. Time per operation efficiency
        prim_efficiency = df['prim_time'] / df['prim_operations']
        kruskal_efficiency = df['kruskal_time'] / df['kruskal_operations']

        ax4.scatter(df['vertices'], prim_efficiency, alpha=0.7, label='Prim', s=60, color='blue')
        ax4.scatter(df['vertices'], kruskal_efficiency, alpha=0.7, label='Kruskal', s=60, color='red')
        ax4.set_xlabel('Number of vertices (n)')
        ax4.set_ylabel('Time per operation (ms/op)')
        ax4.set_title('Algorithm Efficiency: Time per Operation')
        ax4.legend()
        ax4.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig('../results/operations_complexity_analysis.png', dpi=300, bbox_inches='tight')
        plt.show()

    def plot_performance_by_size_category(self, df):
        """Performance analysis by size categories"""
        # Define categories
        def get_category(vertices):
            if vertices <= 50:
                return 'Small (n≤50)'
            elif vertices <= 300:
                return 'Medium (51-300)'
            elif vertices <= 1000:
                return 'Large (301-1000)'
            else:
                return 'Extra Large (n>1000)'

        df['category'] = df['vertices'].apply(get_category)

        fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('MST Algorithms Performance by Graph Size Categories', fontsize=16, fontweight='bold')

        # 1. Average time by category
        category_stats = df.groupby('category').agg({
            'prim_time': 'mean',
            'kruskal_time': 'mean',
            'prim_operations': 'mean',
            'kruskal_operations': 'mean',
            'vertices': 'count'
        }).reset_index()

        x = range(len(category_stats))
        width = 0.35

        bars1 = ax1.bar([i - width/2 for i in x], category_stats['prim_time'], width,
                        label='Prim', alpha=0.8, color='blue')
        bars2 = ax1.bar([i + width/2 for i in x], category_stats['kruskal_time'], width,
                        label='Kruskal', alpha=0.8, color='red')

        # Add values on bars
        for bar in bars1 + bars2:
            height = bar.get_height()
            if not np.isnan(height):
                ax1.text(bar.get_x() + bar.get_width()/2., height + 0.01,
                         f'{height:.1f}', ha='center', va='bottom', fontsize=9)

        ax1.set_xlabel('Graph category')
        ax1.set_ylabel('Average execution time (ms)')
        ax1.set_title('Average Execution Time by Category')
        ax1.set_xticks(x)
        ax1.set_xticklabels(category_stats['category'])
        ax1.legend()
        ax1.grid(True, alpha=0.3)

        # 2. Average operations by category
        bars3 = ax2.bar([i - width/2 for i in x], category_stats['prim_operations'], width,
                        label='Prim', alpha=0.8, color='lightblue')
        bars4 = ax2.bar([i + width/2 for i in x], category_stats['kruskal_operations'], width,
                        label='Kruskal', alpha=0.8, color='lightcoral')

        ax2.set_xlabel('Graph category')
        ax2.set_ylabel('Average number of operations')
        ax2.set_title('Average Operations by Category')
        ax2.set_xticks(x)
        ax2.set_xticklabels(category_stats['category'])
        ax2.legend()
        ax2.grid(True, alpha=0.3)

        # 3. Performance ratio
        category_stats['time_ratio'] = category_stats['prim_time'] / category_stats['kruskal_time']

        x_pos = range(len(category_stats))
        bars5 = ax3.bar(x_pos, category_stats['time_ratio'], alpha=0.7, color='green',
                        label='Time ratio (Prim/Kruskal)')

        # Add values on bars
        for i, bar in enumerate(bars5):
            height = bar.get_height()
            if not np.isnan(height):
                ax3.text(bar.get_x() + bar.get_width()/2., height + 0.01,
                         f'{height:.2f}', ha='center', va='bottom', fontsize=9)
                # Highlight better algorithm
                if height < 1:
                    bar.set_color('limegreen')  # Prim better
                else:
                    bar.set_color('orange')     # Kruskal better

        ax3.axhline(y=1, color='red', linestyle='--', alpha=0.8, linewidth=2, label='Equal performance')
        ax3.set_xlabel('Graph category')
        ax3.set_ylabel('Prim/Kruskal ratio')
        ax3.set_title('Execution Time Ratio by Category\n(green: Prim faster, orange: Kruskal faster)')
        ax3.set_xticks(x_pos)
        ax3.set_xticklabels(category_stats['category'])
        ax3.legend()
        ax3.grid(True, alpha=0.3)

        # 4. Number of graphs by category
        counts = category_stats['vertices']
        bars6 = ax4.bar(x_pos, counts, alpha=0.7, color='purple')

        for i, bar in enumerate(bars6):
            height = bar.get_height()
            if not np.isnan(height):
                ax4.text(bar.get_x() + bar.get_width()/2., height + 0.1,
                         f'{int(height)}', ha='center', va='bottom', fontweight='bold')

        ax4.set_xlabel('Graph category')
        ax4.set_ylabel('Number of graphs')
        ax4.set_title('Number of Test Graphs by Category')
        ax4.set_xticks(x_pos)
        ax4.set_xticklabels(category_stats['category'])
        ax4.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig('../results/performance_by_category.png', dpi=300, bbox_inches='tight')
        plt.show()

    def generate_detailed_report(self, df):
        """Generate detailed performance report"""
        print("=" * 70)
        print("DETAILED MST ALGORITHMS PERFORMANCE REPORT")
        print("=" * 70)

        # Basic statistics
        print(f"\n📊 BASIC STATISTICS:")
        print(f"   Total graphs analyzed: {len(df)}")
        print(f"   Vertices range: {df['vertices'].min()} - {df['vertices'].max()}")
        print(f"   Edges range: {df['edges'].min()} - {df['edges'].max()}")
        print(f"   Average graph density: {df['density'].mean():.3f}")

        # Performance
        print(f"\n⚡ PERFORMANCE:")
        print(f"   Prim - Average time: {df['prim_time'].mean():.2f} ± {df['prim_time'].std():.2f} ms")
        print(f"   Kruskal - Average time: {df['kruskal_time'].mean():.2f} ± {df['kruskal_time'].std():.2f} ms")
        print(f"   Prim - Average operations: {df['prim_operations'].mean():.0f} ± {df['prim_operations'].std():.0f}")
        print(f"   Kruskal - Average operations: {df['kruskal_operations'].mean():.0f} ± {df['kruskal_operations'].std():.0f}")

        # Comparison
        time_ratio = df['prim_time'].mean() / df['kruskal_time'].mean()
        ops_ratio = df['prim_operations'].mean() / df['kruskal_operations'].mean()

        print(f"\n📈 ALGORITHM COMPARISON:")
        print(f"   Time ratio Prim/Kruskal: {time_ratio:.3f}")
        print(f"   Operations ratio Prim/Kruskal: {ops_ratio:.3f}")

        if time_ratio < 1:
            print(f"   🎯 CONCLUSION: Prim is {1/time_ratio:.2f}x faster than Kruskal on average")
        else:
            print(f"   🎯 CONCLUSION: Kruskal is {time_ratio:.2f}x faster than Prim on average")

        # Analysis by categories
        print(f"\n📁 PERFORMANCE BY SIZE CATEGORIES:")
        categories = {
            'Small (n≤50)': (0, 50),
            'Medium (51-300)': (51, 300),
            'Large (301-1000)': (301, 1000),
            'Extra Large (n>1000)': (1001, float('inf'))
        }

        for cat_name, (min_v, max_v) in categories.items():
            cat_df = df[(df['vertices'] > min_v) & (df['vertices'] <= max_v)]
            if len(cat_df) > 0:
                prim_time = cat_df['prim_time'].mean()
                kruskal_time = cat_df['kruskal_time'].mean()
                if kruskal_time > 0:  # Avoid division by zero
                    ratio = prim_time / kruskal_time

                    faster_algo = "Prim" if ratio < 1 else "Kruskal"
                    speed_advantage = 1/ratio if ratio < 1 else ratio

                    print(f"   {cat_name}:")
                    print(f"      Graphs: {len(cat_df)}, Prim: {prim_time:.1f}ms, Kruskal: {kruskal_time:.1f}ms")
                    print(f"      {faster_algo} is {speed_advantage:.2f}x faster")

def main():
    """Main function"""
    analyzer = MSTAnalyzer()

    print("🔍 Loading algorithm execution results...")
    df = analyzer.load_all_results()

    if df.empty:
        print("❌ No data for analysis. Please run Java program first to generate results.")
        return

    print(f"✅ Loaded {len(df)} records from results")

    # Create plots
    print("\n📊 Generating analytical plots...")
    try:
        analyzer.plot_execution_time_analysis(df)
        analyzer.plot_operations_complexity(df)
        analyzer.plot_performance_by_size_category(df)
    except Exception as e:
        print(f"❌ Error during plotting: {e}")
        import traceback
        traceback.print_exc()
        return

    # Generate report
    print("\n📄 Generating detailed report...")
    analyzer.generate_detailed_report(df)

    print(f"\n🎉 Analysis completed!")
    print(f"📁 Plots saved to 'results/' folder:")
    print(f"   - execution_time_analysis.png - Execution time analysis")
    print(f"   - operations_complexity_analysis.png - Computational complexity analysis")
    print(f"   - performance_by_category.png - Performance by categories")

if __name__ == "__main__":
    main()