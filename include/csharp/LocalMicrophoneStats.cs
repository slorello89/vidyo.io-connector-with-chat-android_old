// DO NOT EDIT! This is an autogenerated file. All changes will be
// overwritten!

//	Copyright (c) 2016 Vidyo, Inc. All rights reserved.


using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;
using System.Runtime.InteropServices;

namespace VidyoClient
{
	public class LocalMicrophoneStats{
#if __IOS__
		const string importLib = "__Internal";
#else
		const string importLib = "libVidyoClient";
#endif
		private IntPtr objPtr; // opaque VidyoLocalMicrophoneStats reference.
		public IntPtr GetObjectPtr(){
			return objPtr;
		}
		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetaecEchoCouplingNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetagcAverageGainNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetbitsPerSampleNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		[return: MarshalAs(UnmanagedType.I4)]
		private static extern MediaFormat VidyoLocalMicrophoneStatsGetformatNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetidNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetlocalSpeakerStreamsNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetlocalSpeakerStreamsArrayNative(IntPtr obj, ref int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern void VidyoLocalMicrophoneStatsFreelocalSpeakerStreamsArrayNative(IntPtr obj, int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetnameNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetnoiseSuppressionSnrNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetnumberOfChannelsNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetremoteSpeakerStreamsNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern IntPtr VidyoLocalMicrophoneStatsGetremoteSpeakerStreamsArrayNative(IntPtr obj, ref int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern void VidyoLocalMicrophoneStatsFreeremoteSpeakerStreamsArrayNative(IntPtr obj, int size);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetsampleRateMeasuredNative(IntPtr obj);

		[DllImport(importLib, CallingConvention = CallingConvention.Cdecl)]
		private static extern uint VidyoLocalMicrophoneStatsGetsampleRateSetNative(IntPtr obj);

		public uint aecEchoCoupling;
		public uint agcAverageGain;
		public uint bitsPerSample;
		public MediaFormat format;
		public String id;
		public List<LocalSpeakerStreamStats> localSpeakerStreams;
		public String name;
		public uint noiseSuppressionSnr;
		public uint numberOfChannels;
		public List<RemoteSpeakerStreamStats> remoteSpeakerStreams;
		public uint sampleRateMeasured;
		public uint sampleRateSet;
		public LocalMicrophoneStats(IntPtr obj){
			objPtr = obj;

			List<LocalSpeakerStreamStats> csLocalSpeakerStreams = new List<LocalSpeakerStreamStats>();
			int nLocalSpeakerStreamsSize = 0;
			IntPtr nLocalSpeakerStreams = VidyoLocalMicrophoneStatsGetlocalSpeakerStreamsArrayNative(VidyoLocalMicrophoneStatsGetlocalSpeakerStreamsNative(objPtr), ref nLocalSpeakerStreamsSize);
			int nLocalSpeakerStreamsIndex = 0;
			while (nLocalSpeakerStreamsIndex < nLocalSpeakerStreamsSize) {
				LocalSpeakerStreamStats csTlocalSpeakerStreams = new LocalSpeakerStreamStats(Marshal.ReadIntPtr(nLocalSpeakerStreams + (nLocalSpeakerStreamsIndex * Marshal.SizeOf(nLocalSpeakerStreams))));
				csLocalSpeakerStreams.Add(csTlocalSpeakerStreams);
				nLocalSpeakerStreamsIndex++;
			}

			List<RemoteSpeakerStreamStats> csRemoteSpeakerStreams = new List<RemoteSpeakerStreamStats>();
			int nRemoteSpeakerStreamsSize = 0;
			IntPtr nRemoteSpeakerStreams = VidyoLocalMicrophoneStatsGetremoteSpeakerStreamsArrayNative(VidyoLocalMicrophoneStatsGetremoteSpeakerStreamsNative(objPtr), ref nRemoteSpeakerStreamsSize);
			int nRemoteSpeakerStreamsIndex = 0;
			while (nRemoteSpeakerStreamsIndex < nRemoteSpeakerStreamsSize) {
				RemoteSpeakerStreamStats csTremoteSpeakerStreams = new RemoteSpeakerStreamStats(Marshal.ReadIntPtr(nRemoteSpeakerStreams + (nRemoteSpeakerStreamsIndex * Marshal.SizeOf(nRemoteSpeakerStreams))));
				csRemoteSpeakerStreams.Add(csTremoteSpeakerStreams);
				nRemoteSpeakerStreamsIndex++;
			}

			aecEchoCoupling = VidyoLocalMicrophoneStatsGetaecEchoCouplingNative(objPtr);
			agcAverageGain = VidyoLocalMicrophoneStatsGetagcAverageGainNative(objPtr);
			bitsPerSample = VidyoLocalMicrophoneStatsGetbitsPerSampleNative(objPtr);
			format = VidyoLocalMicrophoneStatsGetformatNative(objPtr);
			id = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoLocalMicrophoneStatsGetidNative(objPtr));
			localSpeakerStreams = csLocalSpeakerStreams;
			name = (string)MarshalPtrToUtf8.GetInstance().MarshalNativeToManaged(VidyoLocalMicrophoneStatsGetnameNative(objPtr));
			noiseSuppressionSnr = VidyoLocalMicrophoneStatsGetnoiseSuppressionSnrNative(objPtr);
			numberOfChannels = VidyoLocalMicrophoneStatsGetnumberOfChannelsNative(objPtr);
			remoteSpeakerStreams = csRemoteSpeakerStreams;
			sampleRateMeasured = VidyoLocalMicrophoneStatsGetsampleRateMeasuredNative(objPtr);
			sampleRateSet = VidyoLocalMicrophoneStatsGetsampleRateSetNative(objPtr);
			VidyoLocalMicrophoneStatsFreeremoteSpeakerStreamsArrayNative(nRemoteSpeakerStreams, nRemoteSpeakerStreamsSize);
			VidyoLocalMicrophoneStatsFreelocalSpeakerStreamsArrayNative(nLocalSpeakerStreams, nLocalSpeakerStreamsSize);
		}
	};
}
