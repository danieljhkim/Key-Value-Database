package cmd

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"

	"github.com/danieljhkim/kv/internal/client"
	"github.com/danieljhkim/kv/internal/config"
)

var (
	cfgFile string
	kvClient *client.Client
	host     string
	port     int
	
	// Interactive mode flag
	interactive bool
)

var rootCmd = &cobra.Command{
	Use:   "kv",
	Short: "KV Client - A command line client for KV Store",
	Long: `A Redis-like distributed key-value store client implemented in Go.
This client connects to a KV server and allows you to send commands.`,
	PersistentPreRun: func(cmd *cobra.Command, args []string) {
		// Initialize client connection before any command
		if cmd.Name() != "help" && cmd.Name() != "version" {
			kvClient = client.NewClient()
			if !kvClient.Connect(host, port) {
				fmt.Println("Failed to connect to server. Exiting.")
				os.Exit(1)
			}
			
			// Setup cleanup on exit
			c := make(chan os.Signal, 1)
			signal.Notify(c, os.Interrupt, syscall.SIGTERM)
			go func() {
				<-c
				fmt.Println("\nShutting down...")
				kvClient.Disconnect()
				os.Exit(0)
			}()
		}
	},
	Run: func(cmd *cobra.Command, args []string) {
		if interactive {
			fmt.Println("Starting interactive mode...")
			kvClient.RunCLI()
		} else {
			cmd.Help()
		}
	},
}

func Execute() error {
	return rootCmd.Execute()
}

func init() {
	cobra.OnInitialize(initConfig)

	rootCmd.PersistentFlags().StringVar(&cfgFile, "config", "", "config file (default is ./config.yaml)")
	// rootCmd.PersistentFlags().StringVar(&host, "host", "", "server host")
	// rootCmd.PersistentFlags().IntVar(&port, "port", 0, "server port")
	
	// Add interactive mode flag
	rootCmd.Flags().BoolVarP(&interactive, "interactive", "i", false, "start in interactive mode")

}

func initConfig() {
	cfg, err := config.Load(cfgFile)
	if err != nil {
		fmt.Printf("Error loading configuration: %v\n", err)
		os.Exit(1)
	}

	if host == "" {
		host = cfg.Server.Host
	}
	if port == 0 {
		port = cfg.Server.Port
	}
	
	// Bind flags to viper for use in other commands
	viper.BindPFlag("server.host", rootCmd.PersistentFlags().Lookup("host"))
	viper.BindPFlag("server.port", rootCmd.PersistentFlags().Lookup("port"))
}