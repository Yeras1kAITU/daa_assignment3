#!/usr/bin/env python3
"""
MST Algorithms Performance Analysis Runner
"""

import subprocess
import sys
import os

def check_and_install_requirements():
    """Check and install required packages"""
    requirements = {
        'pandas': 'pandas',
        'matplotlib': 'matplotlib',
        'seaborn': 'seaborn',
        'scipy': 'scipy',
        'numpy': 'numpy'
    }

    print("🔍 Checking Python dependencies...")

    for package, install_name in requirements.items():
        try:
            __import__(package)
            print(f"   ✅ {package} installed")
        except ImportError:
            print(f"   📦 Installing {package}...")
            try:
                subprocess.check_call([sys.executable, '-m', 'pip', 'install', install_name])
                print(f"   ✅ {package} successfully installed")
            except Exception as e:
                print(f"   ❌ Error installing {package}: {e}")
                return False
    return True

def main():
    """Main function"""
    print("=" * 60)
    print("🚀 MST ALGORITHMS PERFORMANCE ANALYSIS")
    print("=" * 60)

    # Check dependencies
    if not check_and_install_requirements():
        print("❌ Failed to install all dependencies")
        return

    # Check for results
    results_dir = "../results"
    if not os.path.exists(results_dir):
        print(f"❌ Results folder '{results_dir}' not found")
        print("   Please run Java program first to generate results")
        return

    # Run analysis
    print("\n📈 Starting analysis...")
    try:
        from analysis import main as analysis_main
        analysis_main()
    except Exception as e:
        print(f"❌ Error during analysis: {e}")
        return

    print("\n" + "=" * 60)
    print("✅ Analysis completed successfully!")
    print("📊 All plots saved to 'results/' folder")
    print("=" * 60)

if __name__ == "__main__":
    main()